package org.tycoon.config

/**
 * S3a configuration key class
 *
 * @param accessKey s3a access key
 * @param secretKey s3a secret key
 * @param endpoint  s3a endpoint
 */
final case class S3aConfig(
                            accessKey: String,
                            secretKey: String,
                            endpoint: String
                          )
