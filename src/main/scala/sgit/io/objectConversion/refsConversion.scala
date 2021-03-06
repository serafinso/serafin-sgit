package sgit.io.objectConversion

import better.files._
import sgit.io.utilities
import sgit.io.utilities.isFilePresent
import sgit.objects.Ref

object refsConversion {
  /** NOT PF method
   *
   * @param name ref name to search
   * @return The ref with the right name if it exist, none otherwise
   */
  def getRefByName(name : String) : Option[Ref] = {
    if (isFilePresent(".sgit/refs/heads/" + name)){
      val headFile : File = (".sgit/refs/heads/" + name).toFile
      val line : String = headFile.contentAsString
      if (line.equals("")) { //PREMIER COMMIT
        println("ERREUR with last commit")
        None
      } else {
        println("On branch "+ name +"\n")
        Some(Ref(line, name))
      }
    }else {
      println("ref doesn't exist")
      None
    }
  }

  /** NOT PF method
   * create the ref file
   * @param ref to be created
   */
  def createOrUpdateRefFile (ref : Ref) : Unit = {
    if(isFilePresent(".sgit/refs/heads/" + ref.name)){ //Update
      (".sgit/refs/heads/"+ref.name).toFile.overwrite(ref.commitKey)
    }else{ //Create
      val newFileInObject = utilities.createFile(isDirectory = false, ".sgit/refs/heads/" + ref.name)
      if (newFileInObject) {
        (".sgit/refs/heads/" + ref.name).toFile.appendText(ref.commitKey)
      }
    }
  }
}
