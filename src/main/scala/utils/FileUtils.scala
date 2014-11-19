package utils

import java.util.ArrayList
import scala.io.Source
import java.io._

object FileUtils {

  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    if (these != null ) {
    	these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
    } else {
      Array()
    }
  }
}