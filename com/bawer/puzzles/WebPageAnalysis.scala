package com.bawer.puzzles {

  /**
   * A random web page analysis application
   *
   * Query this web service 100 times : http://www.randomwebsite.com/cgi-bin/random.pl
   * The response contains a short HTML document with one link inside. Query this link and store link URL, as well as
   * the returned HTTP response codes in a suitable data structure.
   * The application should output a list of all link URLs ordered by the first character of the domain name (ignore
   * http:// and www prefixes) and also print the related HTTP Code, if it’s not 200.
   */
  object WebPageAnalysis {

    type OptionMap = Map[String, Any]

    private val OPTION_QUERYCOUNT = "--queryCount"
    private val OPTION_SITE = "--site"
    private val OPTION_CLIENTCOUNT = "--clientCount"
    private val OPTION_VERBOSEOUTPUT = "--verboseOutput"
    val defaultOptions = Map(
              OPTION_QUERYCOUNT -> 100,
              OPTION_SITE -> "http://www.randomwebsite.com/cgi-bin/random.pl",
              OPTION_CLIENTCOUNT -> 1,
              OPTION_VERBOSEOUTPUT -> false)
    private val USAGE = "Usage: WebPageAnalysis " // TODO: dynamically create usage



    def main(args: Array[String]) : Unit = {
      val options = parseArguments(args)
      run(options)
    }



    /**
     * parse arguments
     */
    def parseArguments(args: Array[String]) : OptionMap = {
      def parse(map : OptionMap, list: List[String]) : OptionMap = {
        list match {
          case Nil => map
          case OPTION_QUERYCOUNT :: value :: tail =>
                                 parse(map + (OPTION_QUERYCOUNT -> value.toInt), tail)
          case OPTION_SITE :: value :: tail =>
                                 parse(map + (OPTION_SITE -> value), tail)
          case OPTION_CLIENTCOUNT :: value :: tail =>
                                 parse(map + (OPTION_CLIENTCOUNT -> value.toInt), tail)
          case OPTION_VERBOSEOUTPUT :: tail =>
                                 parse(map + (OPTION_VERBOSEOUTPUT -> true), tail)
          case option :: tail => println("Unknown option: " + option)
                                 parse(map, tail)
        }
      }

      if (args.length == 0) defaultOptions
      else if (args(0) == "--help") { println(USAGE); System.exit(0) }
      parse(defaultOptions, args.toList)
    }



    /**
     * main program logic here.
     */
    def run(options: OptionMap) = {

      /**
       * some helper functions
       */
      def readOptionValue[T](optionKey: String) : T = {
        options.get(optionKey) match {
          case Some(x) => x.asInstanceOf[T]
          case _ => defaultOptions.get(optionKey) match {
            case Some(y) => y.asInstanceOf[T]
            case _ => throw new Exception("Internal error: Unexpected option value")
          }
        }
      }
      def getResponseCode(responseCode: Either[Throwable, Int]) = {
        responseCode match {
          case Right(intValue) => if (intValue == 200) "" else intValue.toString()
          case Left(throwableValue) => throwableValue.toString()
        }
      }
      def cleanOutURL(s: String) : String = s.replaceAll("(https?://)?(www.)?", "")


      /**
       * Logging related stuff
       */
      import java.util.Calendar
      val logEnabled = readOptionValue[Boolean](OPTION_VERBOSEOUTPUT)
      def logTime = print("(" + Calendar.getInstance.getTimeInMillis + ")\t")
      def logVerbose(m: => String) = if (logEnabled) { logTime; println(m) }
      def log(m: => String) = { logTime; println(m) }
      def logResults(result: IndexedSeq[(String, String)]) =  {
        log("Results:")
        result.foreach(r => {
          print(r._2)
          print("\t")
          println(r._1)
        })
      }


      /**
       * main logic
       */
      import dispatch._, Defaults._
      def visitSite(site: String) : Future[Either[Throwable, Int]] = {
        val siteURL = url(site)
        Http(siteURL > (x => {
          val statusCode = x.getStatusCode()
          logVerbose("got response " + statusCode + " for site: " + site)
          statusCode
        })).either
      }

      val serviceURL = url(readOptionValue[String](OPTION_SITE))
      def getRandomSite = Http(serviceURL > (x => x.getHeader("Location")))

      val randomSiteVisits = for {
        _ <- 1 to readOptionValue[Int](OPTION_QUERYCOUNT)
        val site = getRandomSite()
        _= logVerbose("visiting site: " + site)
        val futureResponse = visitSite(site)
      }
      yield for ( responseCode <- futureResponse )
        yield ( cleanOutURL(site) -> getResponseCode(responseCode) )

      import scala.concurrent._
      import scala.concurrent.duration._
      import scala.util.Success
      val result = scala.concurrent.Future.sequence(randomSiteVisits)
      logVerbose("Sent request to all sites, now waiting to get responses from all.")
      Await.result(result, Duration.Inf)
      result.value match {
            case Some(Success(t)) => {
              logVerbose("successfully got all results")
              logResults(t.sortBy(x => x._1.head))
            }
            case _ => log("failed for some reason")
      }
    }
  }
}