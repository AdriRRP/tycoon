#!/bin/bash


set -Eeuo pipefail
trap cleanup SIGINT SIGTERM ERR EXIT

script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd -P)

usage() {
  cat <<EOF # remove the space between << and EOF, this is due to web plugin issue
Usage: $(basename "${BASH_SOURCE[0]}") [-h] [-v] -b mybucket -a mc_alias [-t /tmp/extractions] -i /tmp/input_folder

Given a directory path, recursively searches through the .zip files for txt files and uploads them to the selected s3 bucket.

Available options:

-h, --help        Print this help and exit
-v, --verbose     Print script debug info
-b, --bucket      [Mandatory] Target S3 bucket
-a, --mc-alias    [Mandatory] Target mc (minio client) alias with S3 credentials configured
-t, --temp-path   Temporary path where .zip files are extracted (default: /tmp/$(basename ${0%.*})_$(date +%s%N))
-i, --input-path  [Mandatory] Input directory path where target files are located
-m, --minio-bin   Minio client binary path (default: /usr/bin/mc)
-p, --bucket-path Path into the bucket where files are placed (default: root bucket path)
EOF
  exit
}

cleanup() {
  trap - SIGINT SIGTERM ERR EXIT
  # script cleanup here
  rm -rf $temp_path
}

setup_colors() {
  if [[ -t 2 ]] && [[ -z "${NO_COLOR-}" ]] && [[ "${TERM-}" != "dumb" ]]; then
    NOFORMAT='\033[0m' RED='\033[0;31m' GREEN='\033[0;32m' ORANGE='\033[0;33m' BLUE='\033[0;34m' PURPLE='\033[0;35m' CYAN='\033[0;36m' YELLOW='\033[1;33m'
  else
    NOFORMAT='' RED='' GREEN='' ORANGE='' BLUE='' PURPLE='' CYAN='' YELLOW=''
  fi
}

msg() {
  echo >&2 -e "${1-}"
}

die() {
  local msg=$1
  local code=${2-1} # default exit status 1
  msg "$msg"
  exit "$code"
}

parse_params() {
  # Default params
  temp_path=/tmp/$(basename ${0%.*})_$(date +%s%N)
  minio_bin=/usr/bin/mc
  bucket_path=""

  while [[ $# -gt 0 ]]; do
    case "${1}" in
    -h | --help) usage ;;
    -v | --verbose) set -x ;;
    --no-color) NO_COLOR=1 ;;
    -b | --bucket)
      if [ -z "${2-}" ]; then die "--bucket | -b param requires value"; fi
      bucket="${2}"
      shift
      ;;
    -a | --mc-alias)
      if [[ -z "${2-}" ]]; then die "--mc-alias | -a param requires value"; fi
      mc_alias="${2}"
      shift
      ;;
    -t | --temp-path)
      if [[ -z "${2-}" ]]; then die "--temp-path | -t param requires value"; fi
      temp_path="${2}"
      shift
      ;;
    -i | --input-path)
      if [[ -z "${2-}" ]]; then die "--input-path | -i param requires value"; fi
      input_path="${2}"
      shift
      ;;
    -m | --minio-bin)
      if [[ -z "${2-}" ]]; then die "--minio-bin | -m param requires value"; fi
      minio_bin="${2}"
      shift
      ;;
    -p | --bucket-path)
      if [[ -z "${2-}" ]]; then die "--bucket-path | -p param requires value"; fi
      bucket_path="${2}"
      shift
      ;;
    -?*) die "Unknown option: $1" ;;
    *) break ;;
    esac
    shift
  done

  args=("$@")

  # check required params and arguments
  [[ -z "${bucket-}" ]] && die "Missing required parameter: --bucket"
  [[ -z "${mc_alias-}" ]] && die "Missing required parameter: --mc-alias"
  [[ -z "${input_path-}" ]] && die "Missing required parameter: --input-path"

  # check if minio_bin is a valid executable
  command -v "$minio_bin" >/dev/null 2>&1 || die "$minio_bin is not a valid command. Please select your minio client command with --minio-bin parameter."
  return 0
}

parse_params "$@"
setup_colors

# script logic here

msg "${RED}Read parameters:${NOFORMAT}"
msg "-b, --bucket     : ${bucket}"
msg "-a, --mc-alias   : ${mc_alias}"
msg "-t, --temp-path  : ${temp_path}"
msg "-i, --input-path : ${input_path}"
msg "-m, --minio-bin  : ${minio_bin}"
msg "-p, --bucket-path : ${bucket_path}"

s3_path=$mc_alias/$bucket/$bucket_path

msg "S3 final path: ${s3_path}"


minio_put() {
    $minio_bin cp $1 $s3_path/${1##*/}
}

# Initialize temp path
rm -rf $temp_path
mkdir -p $temp_path

# Copy all zipped files to temp directory
msg "Copying zipped files to $temp_path"
find $input_path -name "*.zip" -exec cp "{}" $temp_path/ \;

# Copy to S3 all text files in input directory
for file in $(find $input_path -name '*.txt')
do
    msg "Putting $file to $s3_path/${file##*/}"
    minio_put $file
done

# Uncompress and remove all zipped files
while [[ $(find $temp_path -name '*.zip' | wc -c ) > 0 ]]; do
	for file in $(find $temp_path -name '*.zip')
	do
        msg "Unzipping $file ..." 
		unzip ${file} -d $temp_path
        msg "Removing $file ..." 
		rm ${file}
	done
done

# Copy to S3 all text files in temp directory
for file in $(find $temp_path -name '*.txt')
do
    minio_put $file
done

