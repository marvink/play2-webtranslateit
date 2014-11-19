package de.marvinkerkhoff.sbt.webtranslateit

import javax.swing.Box

import sbt._
import Keys._
import java.net.URL
import java.io.File

class SbtWebtranslateit(prefix: String) extends Plugin {
  val pfx = if (prefix.isEmpty) "" else prefix + "-"

  val projectToken = SettingKey[String](
    pfx + "projectToken",
    "projectToken of webtranslateit-project"
  )

  val languageKeys = SettingKey[Seq[String]](
    pfx + "languageKeys",
    "comma seperated list of language keys"
  )

  val defaultLanguage = SettingKey[String](
    pfx + "defaultLanguage",
    "defaultLanguage that is downloaded without suffix"
  )

  val masterProjectFileId = SettingKey[String](
    pfx + "masterProjectFileId",
    "file id of the master project file"
  )

  val downloadMessages = TaskKey[Seq[File]](
    pfx + "downloadMessages",
    "download messages from webtranslateit"
  )

  val genMessagesSettings: Seq[Setting[_]] = Seq(

    downloadMessages <<= (
      projectToken,
      masterProjectFileId,
      languageKeys,
      defaultLanguage,
      streams
      ) map { (projectToken, masterProjectFileId, languageKeys, defaultLanguage, s) =>

        val generated = SbtWebtranslateit(projectToken, masterProjectFileId, languageKeys, defaultLanguage, s)

      Seq()
    }

  )

  def SbtWebtranslateit(projectToken: String, masterProjectFileId: String, languageKeys: Seq[String], defaultLanguage: String, s: TaskStreams) = {

    for (key <- languageKeys) {
      if (key.equals(defaultLanguage)) {
        s.log.info("Downloading Message Files https://webtranslateit.com/api/projects/"+ projectToken +"/files/"+ masterProjectFileId +"/locales/"+key)
        new URL("https://webtranslateit.com/api/projects/"+ projectToken +"/files/"+ masterProjectFileId +"/locales/"+key) #> new File("conf/messages") !!
      } else {
        s.log.info("Downloading Message Files https://webtranslateit.com/api/projects/"+ projectToken +"/files/"+ masterProjectFileId +"/locales/"+key)
        new URL("https://webtranslateit.com/api/projects/"+ projectToken +"/files/"+ masterProjectFileId +"/locales/"+key) #> new File("conf/messages."+key) !!
      }
    }
  }

}

object SbtWebtranslateit extends SbtWebtranslateit("")
