#! /usr/bin/python3

import sys
import os
import subprocess

classpath = ""

################################################################################
def compileCompiler() :
  print("Compiling Compiler.java...", end="", file=sys.stderr)
  returnCode = subprocess.Popen("cd ../src/ && javac Compiler.java", shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).wait()
  if returnCode == 0 :
    print("Done", file=sys.stderr)
  else :
    print("ERROR !", file=sys.stderr)
  print("", file=sys.stderr)
################################################################################

################################################################################
def deleteClasses() :

  for root, subdirs, files in os.walk("..") :
    if ".git" in root :
      continue
    for filename in files :
      if os.path.splitext(filename)[1] == ".class" :
        os.remove(root+"/"+filename)
        
  return classpath
################################################################################

################################################################################
def findClasspath() :
  global classpath

  if len(classpath) > 0 :
    return classpath

  for root, subdirs, files in os.walk("..") :
    if ".git" in root :
      continue
    for filename in files :
      if os.path.splitext(filename)[1] == ".class" :
        classpath += ("" if len(classpath) == 0 else ":") + root
        break
        
  return classpath
################################################################################

################################################################################
def compiler() :
  return "java -classpath %s Compiler"%findClasspath()
################################################################################

################################################################################
def green(string) :
  return "\033[92m%s\033[0m"%string
################################################################################

################################################################################
def purple(string) :
  return "\033[95m%s\033[0m"%string
################################################################################

################################################################################
def red(string) :
  return "\033[91m%s\033[0m"%string
################################################################################

################################################################################
def changeExtension(filename, newExtension) :
  return os.path.splitext(filename)[0] + newExtension
################################################################################

################################################################################
def findInputFiles() :
  inputFiles = []
  for filename in os.listdir('input') :
    if os.path.splitext(filename)[1] == ".l" :
      inputFiles.append(filename)
  return inputFiles
################################################################################

################################################################################
def deleteCompilationOutputs() :
  outputExtensions = [".sa", ".sc", ".ts", ".nasm", ".pre-nasm", ".c3a", ".fg", ".fgs", ".ig"]
  for filename in os.listdir('input') :
    if os.path.splitext(filename)[1] in outputExtensions :
      os.remove("input/"+filename)
################################################################################

################################################################################
def compileInputFiles(inputFiles) :
  for inputFile in inputFiles :
    print("Compiling %s..."%inputFile, end="", file=sys.stderr)
    returnCode = subprocess.Popen("{} input/{}".format(compiler(), inputFile), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).wait()
    if returnCode == 0 :
      print("Done", file=sys.stderr)
    else :
      print("ERROR !", file=sys.stderr)
  print("", file=sys.stderr)
################################################################################

################################################################################
def getNewEvaluationResult(name) :
  return [name, {"correct" : [], "incorrect" : [], "notfound" : []}]
################################################################################

################################################################################
def evaluateSa(inputFiles) :
  evaluation = getNewEvaluationResult("Syntaxe Abstraite")
  compareArbres = "compare_arbres/compare_arbres_xml"
  if not os.path.isfile(compareArbres) :
    print("Executable non trouvé : %s (il faut le compiler)"%compareArbres, file=sys.stderr)
    exit(1)

  for filename in inputFiles :
    saFilename = changeExtension(filename, ".sa")
    if not os.path.isfile("input/"+saFilename) :
      evaluation[1]["notfound"].append(saFilename)
      continue
    
    saRef = "sa-ref/"+saFilename
    if not os.path.isfile(saRef) :
      print("Fichier non trouvé : %s"%saRef, file=sys.stderr)
      exit(1)

    res = subprocess.Popen("{} {} input/{}".format(compareArbres, saRef, saFilename), shell=True, stdout=open(os.devnull, "w"), stderr=subprocess.PIPE).stderr.read()
    if "egaux" in str(res) :
      evaluation[1]["correct"].append(saFilename)
    else :
      evaluation[1]["incorrect"].append(saFilename)

  return evaluation
################################################################################

################################################################################
def evaluateDiff(inputFiles, extension, path, name) :
  evaluation = getNewEvaluationResult(name)

  for filename in inputFiles :
    producedFile = changeExtension(filename, extension)
    if not os.path.isfile("input/"+producedFile) :
      evaluation[1]["notfound"].append(producedFile)
      continue
    
    ref = path+producedFile
    if not os.path.isfile(ref) :
      print("Fichier non trouvé : %s"%ref, file=sys.stderr)
      exit(1)

    res = subprocess.Popen("diff {} input/{}".format(ref, producedFile), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).stdout.read()
    if len(res.strip()) == 0 :
      evaluation[1]["correct"].append(producedFile)
    else :
      evaluation[1]["incorrect"].append(producedFile)

  return evaluation
################################################################################

################################################################################
def printListElements(destination, elements, colorFunction, useColor, resultStr) :
  if len(elements) == 0 :
    return
  maxColumnSize = len(max(elements, key=len))
  for filename in elements :
    if useColor :
      print("\t{}".format(colorFunction(filename)), file=destination)
    else :
      print("\t{:{}} {}".format(filename, maxColumnSize+2, resultStr), file=destination)
################################################################################

################################################################################
def printEvaluationResult(destination, evaluationResult, useColor) :
  name = evaluationResult[0]
  correct = evaluationResult[1]["correct"]
  incorrect = evaluationResult[1]["incorrect"]
  notfound = evaluationResult[1]["notfound"]

  nbCorrect = len(correct)
  nbTotal = len(correct) + len(incorrect) + len(notfound)

  print("Évaluation de %s :"%name, file=destination)
  print("{}/{} correct ({:6.2f}%)".format(nbCorrect, nbTotal, 100.0*nbCorrect/nbTotal), file=destination)
  printListElements(destination, correct, green, useColor, "CORRECT")
  printListElements(destination, incorrect, purple, useColor, "INCORRECT")
  printListElements(destination, notfound, red, useColor, "NON-EXISTANT")
################################################################################

################################################################################
if __name__ == "__main__" :

  inputFiles = findInputFiles()
  deleteCompilationOutputs()

  compileCompiler()
  compileInputFiles(inputFiles)
  deleteClasses()

  saEvaluation = evaluateSa(inputFiles)
  tsEvaluation = evaluateDiff(inputFiles, ".ts", "ts-ref/", "Table des Symboles")

  useColor = True

  if useColor :
    print("Légende : {}  {}  {}".format(green("CORRECT"), purple("INCORRECT"), red("NON-EXISTANT")))

  printEvaluationResult(sys.stdout, saEvaluation, useColor)
  printEvaluationResult(sys.stdout, tsEvaluation, useColor)
################################################################################

