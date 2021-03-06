package jupai9.examples;

import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Demonstrates analysing CSV data by executing SQL like queries in Apache Spark SQL.
 */
// spark sql handel csv like database using standard sql 
public class CSVFileAnalysisInSparkSQL {
// reading csv 
    public static void main(String[] args) {
        // Create Spark Session to create connection to Spark(must be written in all spark program)
        // ap name : name of session master :number of cores
        final SparkSession sparkSession = SparkSession.builder ().appName ("Spark CSV Analysis Demo").master ("local[2]")
                .getOrCreate ();

        // Get DataFrameReader using SparkSession
        final DataFrameReader dataFrameReader = sparkSession.read ();
        // Set header option to true to specify that first row in file contains
        // name of columns
        // true if data contain header 
        dataFrameReader.option ("header", "true");
        //reading csv and make table in memory (csvDataFrame)
        final Dataset<Row> csvDataFrame = dataFrameReader.csv ("src/main/resources/data.csv");
        // Print Schema to see column names, types and other metadata
        csvDataFrame.printSchema ();

        // Create view and execute query to convert types as, by default, all columns have string types
        csvDataFrame.createOrReplaceTempView ("ROOM_OCCUPANCY_RAW");
        final Dataset<Row> roomOccupancyData = sparkSession
                .sql ("SELECT CAST(id as int) id, CAST(date as string) date, CAST(Temperature as float) Temperature, "
                        + "CAST(Humidity as float) Humidity, CAST(Light as float) Light, CAST(CO2 as float) CO2, "
                        + "CAST(HumidityRatio as float) HumidityRatio, CAST(Occupancy as int) Occupancy FROM ROOM_OCCUPANCY_RAW");

        // Print Schema to see column names, types and other metadata
        roomOccupancyData.printSchema ();

        // Create view to execute query to get filtered data
        roomOccupancyData.createOrReplaceTempView ("ROOM_OCCUPANCY");
        sparkSession.sql ("SELECT * FROM ROOM_OCCUPANCY WHERE Temperature >= 23.6 AND Humidity > 27 AND Light > 500 "
                + "AND CO2 BETWEEN 920 and 950").show(10);
    }
}