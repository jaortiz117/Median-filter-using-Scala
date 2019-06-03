import java.awt.{BorderLayout, GridLayout, Label}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import filters._
import javax.swing.{ImageIcon, JFileChooser, JFrame, JLabel}

object ClientMain extends App {

  //open frame
  val frame = new JFrame("Image Filter")
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

  val label1 = new JLabel("Choose Image to Process")
  frame.getContentPane.add(label1, BorderLayout.CENTER)
  frame.setSize(480, 480)
  frame.setVisible(true)

  //open file chooser
  val fc = new JFileChooser()
  fc.showOpenDialog(null)

  //file path
  var input: String = fc.getSelectedFile.getAbsolutePath

  //check for correct file
  val errorF = new JFrame("Image Filter")
  errorF.setSize(480, 480)
  errorF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

  //keep checking until correct file is chosen
  var count = 0
  while (!(input.contains("jpeg") || input.contains("png") || input.contains("jpg"))) {
    val errorL = new JLabel("wrong format chosen \n Try again")
    errorF.getContentPane.add(errorL, BorderLayout.CENTER)
    errorF.setVisible(true)
    fc.showOpenDialog(null)
    input = fc.getSelectedFile.getAbsolutePath

    count += 1
    if (count > 2) { //if fails 3 times app closes
      System.exit(0)
    }
  }

  //////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////
  //Begin executing filters
  //////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////

  //start counter
  val startTime = currentTime

  // (a) create futures

  val serialFuture = getImages(input, SerialFilter)
  val parallelFuture = getImages(input, ParallelFilter)
  // (b) get a combined result in a for-comprehension
  val result: Future[(ProcessedImage, ProcessedImage)] = for {
    serial <- serialFuture
    parallel <- parallelFuture
  } yield (serial, parallel)

  // (c) do whatever you need to do with the results
  val resultF = new JFrame("Image Filter")
  resultF.setSize(1000, 900)
  resultF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  resultF.setLocation(500, 100)
  val origImage = new JLabel(new ImageIcon(input))

  resultF.getContentPane.add(origImage)

  result.onComplete {
    case Success(x) => {
//      val endTime = deltaTime(startTime)
      val newImage1 = new JLabel(new ImageIcon(x._1.image))
      val newImage2 = new JLabel(new ImageIcon(x._2.image))

      val origString = new JLabel("Original Image")
      val newString1 = new JLabel("Image 1, Running time: " + x._1.timeDifference + "\n " + x._1.name)
      val newString2 = new JLabel("Image 2, Running time: " + x._2.timeDifference + "\n " + x._2.name)


      resultF.getContentPane.add(newImage1)
      resultF.getContentPane.add(newImage2)

      resultF.getContentPane.add(origString)
      resultF.getContentPane.add(newString1)
      resultF.getContentPane.add(newString2)

      resultF.setLayout(new GridLayout(2, 3))
      resultF.setVisible(true)

    }
    case Failure(e) => e.printStackTrace
  }

  // important for a little parallel demo: need to keep
  // the jvm’s main thread alive
  sleep(5000)


  def currentTime = System.currentTimeMillis()

  def deltaTime(t0: Long) = currentTime - t0

  def sleep(time: Long): Unit = Thread.sleep(time)

  // a simulated web service
  def getImages(inPath: String, filter: Filter): Future[ProcessedImage] = Future {
    //call function
    val outPath = inPath.take(inPath.lastIndexOf(".")) + filter.name + "_filtered.jpg"
    //return image instance
    filter.filter(inPath, outPath)
    new ProcessedImage(ImageIO.read(new File(outPath)), deltaTime(startTime), filter.name)
  }

}
