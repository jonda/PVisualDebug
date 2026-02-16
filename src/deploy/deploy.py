import os
import shutil
sketchbook="\\\\SR-Disk-2\\Hem0010\\dahjon\\Documents\\Processing\\";

#sketchbook="C:/googledrive/procsketchbook/";
base=sketchbook+"libraries/PVisual/"
#os.chdir("../..")
print("--------------------------")
print("Installerar nu PVisual libbet, aktuell mapp är")
origDir = os.getcwd()
print(origDir)
os.chdir("src")
for filename in os.listdir("."):
    if filename.endswith('.java'):
        shutil.copy(filename, base+"src")
        print(filename)
os.chdir("..")

src="dist/PVisual.jar"
dst=base+"library/PVisual.jar"

print(os.getcwd())
print("kopierar "+src + " till " + dst)
print(os.listdir("\\\\SR-Disk-2\\Hem0010\\dahjon\\Documents\\Processing\\libraries\\PVisual/"))
shutil.copyfile(src,dst)

import zipfile

def zipdir(path, ziph):
    # ziph is zipfile handle
    for root, dirs, files in os.walk(path):
        for file in files:
            ziph.write(os.path.join(root, file).replace("\\","/"))
os.chdir(base+"/..")
print(os.getcwd())

zipf = zipfile.ZipFile('PVisual.zip', 'w', zipfile.ZIP_DEFLATED)
zipdir("PVisual", zipf)
zipf.close()
print("--------------------------")
print("Installerar nu PVIsualTool")

base=sketchbook+"tools/PVisualTool/"
#os.chdir("../..")
os.chdir(origDir)
print(os.getcwd())
os.chdir("src")
for filename in os.listdir("."):
    if filename.endswith('.java'):
        shutil.copy(filename, base+"src")
        print(filename)
os.chdir("..")

src="dist/PVisual.jar"
dst=base+"tool/PVisualTool.jar"

print(os.getcwd())
print("kopierar "+src + " till " + dst)
print(os.listdir("\\\\SR-Disk-2\\Hem0010\\dahjon\\Documents\\Processing\\tools\\PVisualTool/"))
shutil.copyfile(src,dst)


zipf = zipfile.ZipFile('PVisualTool.zip', 'w', zipfile.ZIP_DEFLATED)
zipdir("PVisualTool", zipf)
zipf.close()



print ("Installationsprogrammet avslutas")
print ("run Processing")
os.system("\"C:/Program Files/Processing 4.3/processing.exe\" \\\\SR-Disk-2\\Hem0010\\dahjon\\Documents\\Processing\\testPVisual\\testPVisual.pde")
