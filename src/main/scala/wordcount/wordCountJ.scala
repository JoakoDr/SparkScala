/*
Created by Joaquin Diaz
03/04/2019
 */
package wordcount

import org.apache.spark.{SparkConf, SparkContext}

object wordCountJ {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("wordCount").setMaster("local")
    val sc = new SparkContext(conf)
    val inputFile = "/Users/joaquin.diaz.ramirez/Desktop/data/alberto.txt"
    val outputFile = "/Users/joaquin.diaz.ramirez/Desktop/data/alberto"

    val data = sc.textFile(inputFile)
      .map(line => line.split(","))
      .map(userRecord => (userRecord(0),
        userRecord(1), userRecord(2), userRecord(3), userRecord(4)))
    data.collect().foreach(println)

    //Mapeo el inputfile para filtrar los users con una profesion
    val uniqueProfessions = data.map {case (id, age, gender, profession,zipcode) => profession}.distinct().count()
    println(uniqueProfessions)
    //Mapeo el inputfile para filtrar por profesion
    val usersByProfession = data.map{ case (id, age, gender, profession,zipcode) => (profession, 1) }
      .reduceByKey(_ + _).sortBy(-_._2)
    usersByProfession.collect().foreach(println)
    //Mapeo el inputfile para filtrar los generos de los users
    val usersByGender = data.map{ case (id, age, gender, profession,zipcode) => (gender, 1) }
      .reduceByKey(_ + _).sortBy(-_._2)
    usersByGender.collect().foreach(println)
  }
}
