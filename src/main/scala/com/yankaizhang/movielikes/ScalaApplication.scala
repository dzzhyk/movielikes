package com.yankaizhang.movielikes

import java.text.DateFormat
import java.util.{Date, Locale}

import org.slf4j.LoggerFactory

object ScalaApplication {

  private val log = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    log.info("hello")
    println("hello!")
    val now = new Date
    val df = DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA)
    println(df.format(now))

    for (a <- 1 to 10; b <- 2 to 6) {
      println(a * b)
    }

    var c = 0
    while (c < 10) {
      println(c)
      c += 1

    }

    var d = 0
    do {
      println(d)
      d += 1
    } while (d < 10)
  }

}