package org.tycoon.preprocessor


object TextPreProcessor {

  def fix(fileContent: String): String = {
    (removeGarbageLines _ andThen fixPlayerIdentifiers)(fileContent)
  }

  private def fixPlayerIdentifiers(fileContent: String): String = {
    val idSubstitutionsMap = findPlayerIdentifiers(fileContent).map(id => id -> sha256Hash(id))
    idSubstitutionsMap.foldLeft(fileContent) { case (acc, (currId, currHash)) => acc.replaceAllLiterally(currId, currHash) }
  }

  private def findPlayerIdentifiers(fileContent: String): List[String] = {
    val playerIdPattern = "^Seat\\s+\\d+:\\s+(.*)\\s+\\([$€]?\\d+\\.?\\d* in chips\\).*$".r
    fileContent.split("\n").foldLeft(List.empty[String]) { case (acc, curr) =>
      curr match {
        case playerIdPattern(playerId) if !acc.contains(playerId) => playerId :: acc
        case _ => acc
      }
    }
  }

  private def removeGarbageLines(fileContent: String): String = {
    val chatPattern = "^.*said,\\s+\".*?\"$".r
    val otherPattern =
      "^^(?:(?!\\bSeat\\b|\\bPokerStars\\b)[^\\n:])+\\s*:(?:(?!\\bbets\\b|\\bfolds\\b|\\bcalls\\b|\\bposts\\b|\\braises\\b|\\bchecks\\b|\\bshows\\b|\\bmucks\\b|\\bdoesn't\\b).)+$".r

    fileContent.split("\n").filterNot( line =>
      chatPattern.findFirstIn(line).nonEmpty ||
        otherPattern.findFirstIn(line).nonEmpty
    ).mkString("", "\n", "\n")
  }

  private def sha256Hash(text: String): String =
    String.format(
      "%064x",
      new java.math.BigInteger(
        1,
        java.security.MessageDigest.getInstance("SHA-256").digest(text.getBytes("UTF-8"))
      )
    )


}
