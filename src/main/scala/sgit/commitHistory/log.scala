package sgit.commitHistory

import sgit.io.objectConversion.{blobConversion, commitConversion}
import sgit.localChange.diff
import sgit.objectManipulation.{blobManipulation, commitManipulation}
import sgit.objects.{Blob, Commit}

object log {
  /** NOT PF Method
   *
   * @param commit commit to be printed
   * print the commit beautifully
   */
  def printCommit(commit : Commit) : Unit = {
    println(
      Console.YELLOW + "commit "+ commit.key +" ("
        + Console.CYAN + " HEAD -> "
        + Console.GREEN +"master"
        + Console.YELLOW+")"
        + Console.BLACK + "\n\n    " + commit.messageC + "\n"
    )
  }

  /**
   * The main log method with no option
   */
  def simpleLog() : Unit = {
    val optLastCommit : Option[Commit] = commitConversion.getLastCommit
    if (optLastCommit.isDefined){
      val allCommit = commitConversion.getAllCommitFromLast(optLastCommit.get).reverse
      allCommit.reverse.foreach(c=> printCommit(c))
    }else {
      println("fatal: your current branch 'master' does not have any commits yet")
    }
  }

  /** NOT PF method
   *
   * @param b blob added
   * Print the blob added
   */
  def printAdd (b: Blob) : Unit = {
    val content : Option[String] = blobConversion.getBlobContent(b)
    println(
      "diff --git a/"+ b.path +"b/" +b.path +"\n"+
        "new file \n" +
        "index 0000000.."+ b.key.substring(0,7) + "\n" +
        "--- /dev/null\n"+
        "+++ b/"+ b.path + "\n"+
      Console.CYAN + "@@ -0,0 +1,"+content.get.split("\n").length +" @@"+ Console.BLACK

    )
    if(content.isDefined)
      content.get.split("\n").foreach(line =>
        println(Console.GREEN + "-" + line + Console.BLACK)
      )
  }

  /**
   * The main log method
   */
  def logP(): Unit = {
    val optLastCommit : Option[Commit] = commitConversion.getLastCommit
    if (optLastCommit.isDefined){
      val allCommit = commitConversion.getAllCommitFromLast(optLastCommit.get).reverse
      val firstCommit = allCommit.head
      val commitTuple : List[(Commit, Commit)] = commitManipulation.getCommitInTuple(allCommit)
      //ATTENTION PAS DE HEAD
      commitTuple.reverse.foreach(commitTuple => {
        printCommit(commitTuple._2)
        val oldBlobs : Option[List[Blob]] = blobConversion.getBlobFromCommit(commitTuple._1)
        val newBlobs : Option[List[Blob]] = blobConversion.getBlobFromCommit(commitTuple._2)
        if(oldBlobs.isDefined && newBlobs.isDefined){
          diff.diffListBlob(oldBlobs.get, newBlobs.get)
          val addedBlob : List[Blob] = blobManipulation.diffList(newBlobs.get, oldBlobs.get)
          addedBlob.foreach(a => printAdd(a))
        }
        println()
      })

      printCommit(firstCommit)
      val firstCommitBlobs : Option[List[Blob]] = blobConversion.getBlobFromCommit(firstCommit)
      firstCommitBlobs.get.foreach(a => printAdd(a))
    }else {
      println("fatal: your current branch 'master' does not have any commits yet")
    }
  }
}
