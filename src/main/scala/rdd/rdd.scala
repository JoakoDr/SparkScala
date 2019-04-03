package rdd

import org.apache.spark.{SparkConf, SparkContext}

case class Auction(auctionid: String, bid: Float, bidtime: Float, bidder: String, bidderrate: Integer, openbid: Float, price: Float, item: String, daystolive: Integer)

object rdd extends Serializable {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("rdd").setMaster("local")
    val sc = new SparkContext(conf)
    val ebayText = sc.textFile("/Users/joaquin.diaz.ramirez/Desktop/data/rdd.txt")
    //Print first element of the new RDD created
    println(ebayText.first())
    val ebay = ebayText.map(_.split(",")).map(p => Auction(p(0), p(1).toFloat, p(2).toFloat, p(3), p(4).toInt, p(5).toFloat, p(6).toFloat, p(7), p(8).toInt))
    println(ebay.first())
    // Return the number of elements in the RDD
    println(ebay.count())
    //val auction = ebay.toDF() == val auction = sc.textFile("ebay.csv").map(_.split(",")).map(p =>
    //Auction(p(0),p(1).toFloat,p(2).toFloat,p(3),p(4).toInt,p(5).toFloat,p(6).toFloat,p(7),p(8).toInt )).toDF()

    // val caseClassDS = Seq(Auction("5034593242x",50,1.15.toFloat,"albertoTheGame",1,2,70.33.toFloat,"xbox",5))
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    // this is used to implicitly convert an RDD to a DataFrame.
    import sqlContext.implicits._
    // Import Spark SQL data types and Row.
    import org.apache.spark.sql._
    //Convert from a file to a DataFrame in sql
    val auction = sc.textFile("/Users/joaquin.diaz.ramirez/Desktop/data/rdd.txt").map(_.split(",")).map(p =>
      Auction(p(0), p(1).toFloat, p(2).toFloat, p(3), p(4).toInt, p(5).toFloat, p(6).toFloat, p(7), p(8).toInt)).toDF()
    //show the table
    auction.show()
    //print the schema
    auction.printSchema()
    //Group by id and item, and count everything
    auction.groupBy("auctionid", "item").count.show
    //filter with price
    val highprice= auction.filter("price > 100")
    highprice.show()
    //register temptable to use it in sql
    auction.registerTempTable("auction")
    //doing query
    val results =sqlContext.sql("SELECT auctionid, item,  count(bid) FROM auction GROUP BY auctionid, item")
    // display dataframe in a tabular format
    results.show()
    //using a library created by databricks
    val df = sqlContext.load("com.databricks.spark.csv", Map("path" -> "products.csv", "header" -> "true"))
    //take the element with index: 1
    df.take(1)
    df.printSchema()
    df.show()
    //Select the col Categoria and count the distintc
    df.select("Categoria").distinct.count
    // register as a temp table inorder to use sql
    df.registerTempTable("products")
    // How many categories are there
    sqlContext.sql("SELECT distinct Categoria FROM products").collect().foreach(println)
    //counting categorias
    val t =  sqlContext.sql("SELECT Categoria , count(Categoria) as X FROM products group by Categoria order by X desc limit 10")
    t.show()
//Print the physical plan of id in auction table
    auction.select("auctionid").distinct.explain()
    df.select("Id").distinct().explain()
  }

}
