package org.tycoon.utils

import java.util.{Collections, Map => JavaMap}
import scala.jdk.CollectionConverters._

/**
 * Allow multi-platform environment variables manipulation
 */
trait EnvHacker {
  /**
   * Portable method for setting env vars on both *nix and Windows.
   *
   * @see http://stackoverflow.com/a/7201825/293064
   */
  def setEnv(newEnv: Map[String, String]): Unit = {
    try {
      val processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment")
      val theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment")
      theEnvironmentField.setAccessible(true)
      val env = theEnvironmentField.get(null).asInstanceOf[JavaMap[String, String]]
      env.putAll(newEnv.asJava)
      val theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment")
      theCaseInsensitiveEnvironmentField.setAccessible(true)
      val cienv = theCaseInsensitiveEnvironmentField.get(null).asInstanceOf[JavaMap[String, String]]
      cienv.putAll(newEnv.asJava)
    } catch {
      case e: NoSuchFieldException =>
        try {
          val classes = classOf[Collections].getDeclaredClasses
          val env = System.getenv()
          for {cl <- classes} {
            if (cl.getName == "java.util.Collections$UnmodifiableMap") {
              val field = cl.getDeclaredField("m")
              field.setAccessible(true)
              val obj = field.get(env)
              val map = obj.asInstanceOf[JavaMap[String, String]]
              map.clear()
              map.putAll(newEnv.asJava)
            }
          }
        } catch {
          case e2: Exception => e2.printStackTrace()
        }

      case e1: Exception => e1.printStackTrace()
    }
  }
}