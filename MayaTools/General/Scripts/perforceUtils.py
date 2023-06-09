import maya.cmds as cmds
from P4 import P4,P4Exception
import os, cPickle
from functools import partial

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
def p4_getLatestRevision(fileName, *args):
    fileArg = fileName
    #try to connect
    p4 = P4()
    
    try:
        p4.connect()
        
    except:
        cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to connect to perforce server.")
        return
    
    #find currently opened file name
    if fileName == None:
	fileName = cmds.file(q = True, sceneName = True)
	
    syncFiles = []
    
    
    try:
	#client info
	spec = p4.run( "client", "-o" )[0]
	client = spec.get("Client")
	owner = spec.get("Owner")	
	p4.user = owner
	p4.client = client
        
    except:
        cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to obtain client spec information.")
    
    
    
    #try to get the current file revision on local, and compare to depot
    try:
        #Find out the revision of the local version of the file
        myFile = p4.run_have(fileName)[0]
        
        #This will find the revision number of your local file.
        localRevision = int(myFile['haveRev'])
        
        #find out the revision number of the depot version of the file
        depotVersion = p4.run_files(myFile['depotFile'])[0]
        
        #find the depot file path
        depotFile = depotVersion['depotFile']
        
        #find the depot revision number of the file
        depotRevision = int(depotVersion['rev'])        
    
        #check for latest
        if localRevision != depotRevision:
            syncFiles.append(depotFile)
                
                
        
        #Check for scene references in the file
        allRefs = []
        references = cmds.file(q = True, reference = True)
        for reference in references:
            nestedRef = cmds.file(reference, q = True, reference = True)
            allRefs.append(reference)
            allRefs.append(nestedRef)
            
        #loop through all found references and check for latest
        for ref in allRefs:
            #get revision of local file
            myFile = p4.run_have(ref)[0]
            
            #get revision number
            localRefRevision = int(myFile['haveRev']) 
            
            #grab depot file info
            depotRefVersion = p4.run_files(myFile['depotFile'])[0]
            
            #depot file path
            depotFile = depotRefVersion['depotFile']
            
            #get depot's revision #
            depotRefRevision = int(depotRefVersion['rev'])
            
            
            #compare
            if localRefRevision != depotRefRevision:
                syncFiles.append(depotFile)
            
    
        
        
        #if there are files to sync, do it now
        if len(syncFiles) > 0:
            message = "The following files are not at latest revision:\n\n"
            for file in syncFiles:
                message += file + "\n"
                
            result = cmds.confirmDialog(title = "Perforce", icon = "warning", message =  message, button = ["Sync", "Cancel"])
            
            if result == "Sync":
                #sync files
                for f in syncFiles:
                    p4.run_sync(f)
                    
                #ask if user would like to reopen
		if fileArg == None:
		    result = cmds.confirmDialog(title = "Perforce", icon = "question", message =  "Sync Complete. Reopen file to get changes?", button = ["Yes", "Cancel"])
		    
		    if result == "Yes":
			cmds.file(fileName, open = True, force = True)
		    
	else:
	    cmds.confirmDialog(title = "Perforce", icon = "information", message = "This file is already at head revision.", button = "Close")
                    
        #disconnect from server
        p4.disconnect()
                
        
        
    #Handle any p4 errors that come back from trying to run the above code
    except P4Exception:
        errorString = "The following errors were encountered:\n\n"
        
        for e in p4.errors:  
            errorString += e + "\n"
        
        cmds.confirmDialog(title = "Perforce", icon = "critical", message = errorString)
	p4.disconnect()
        return 
    
    
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
def p4_checkOutCurrentFile(fileName, *args):
    fileArg = fileName
    #try to connect
    p4 = P4()
    
    try:
        p4.connect()
        
    except:
        cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to connect to perforce server.")
        return False
    
    #find currently opened file name
    if fileName == None:
	fileName = cmds.file(q = True, sceneName = True)
	
    reopen = False
    syncFiles = []
    
    
    try:
	#client info
	spec = p4.run( "client", "-o" )[0]
	client = spec.get("Client")
	owner = spec.get("Owner")	
	p4.user = owner
	p4.client = client
        
    except:
        cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to obtain client spec information.")
        
    
    try:
        #check to see if file is at head revision
        myFile = p4.run_have(fileName)[0]
        
        #This will find the revision number of your local file.
        localRevision = int(myFile['haveRev'])
        
        #find out the revision number of the depot version of the file
        depotVersion = p4.run_files(myFile['depotFile'])[0]
        
        #find the depot file path
        depotFile = depotVersion['depotFile']
        
        #find the depot revision number of the file
        depotRevision = int(depotVersion['rev'])        
    
        #check for latest
        if localRevision != depotRevision:
            result = cmds.confirmDialog(title = "Perforce", icon = "warning", message = "This file is not at head revision. Please get latest and try again.", button = ["Get Latest", "Cancel"])
            
            if result == "Get Latest":
                p4_getLatestRevision(fileArg)
                p4.disconnect()
		
            else:
                return False
            
        else:
	    try:
		#check to see if file is checked out
		opened = p4.run_opened(depotFile)
		
		if len(opened) > 0:
		    user = opened[0]['user']
		    cmds.confirmDialog(title = "Perforce", icon = "warning", message = "This file is already checked out by: " + user, button = "Close")
		    p4.disconnect()
	    
		else:
		    #check out the file
		    p4.run_edit(depotFile)
		    cmds.confirmDialog(title = "Perfoce", icon = "information", message = "This file is now checked out.", button = "Close")
		    p4.disconnect()
		    #tools path
		    toolsPath = cmds.internalVar(usd = True) + "mayaTools.txt"
		    if os.path.exists(toolsPath):
			
			f = open(toolsPath, 'r')
			mayaToolsDir = f.readline()
			f.close()
			
		    return True
		    
            #Handle any p4 errors that come back from trying to run the above code
            except P4Exception:
                errorString = "The following errors were encountered:\n\n"
                
                for e in p4.errors:  
                    errorString += e + "\n"
                
                cmds.confirmDialog(title = "Perforce", icon = "critical", message = errorString)
                return False
            


    #Handle any p4 errors that come back from trying to run the above code
    except P4Exception:
        errorString = "The following errors were encountered:\n\n"
        
        for e in p4.errors:  
            errorString += e + "\n"
        
        cmds.confirmDialog(title = "Perforce", icon = "critical", message = errorString)
	p4.disconnect()
        return False
    
    
    
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
def p4_getRevisionHistory(*args):
    
    #try to connect
	p4 = P4()
	
	try:
	    p4.connect()
	    
	except:
	    cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to connect to perforce server.")
	    return
	
	#find currently opened file name
	clientFile = cmds.file(q = True, sceneName = True)
	reopen = False
	syncFiles = []
	
	
	try:
	    #client info
	    spec = p4.run( "client", "-o" )[0]
	    client = spec.get("Client")
	    owner = spec.get("Owner")	
	    p4.user = owner
	    p4.client = client    

	except:
	    cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to obtain client spec information.")
	    
	    
	#get revision history of current file
	try:
	    #check to see if file is at head revision
	    myFile = p4.run_have(clientFile)[0]
	    depotVersion = p4.run_files(myFile['depotFile'])[0]
	    depotFile = depotVersion['depotFile']	    
	    history = p4.run_changes(depotFile)
	    
	    info = ""
	    
	    for h in history:
		user = h.get("user")
		change = h.get("change")
		desc = h.get("desc")
		
		if desc.find("\n") == -1:
		    desc = desc + "...\n"
		    
		else:
		    desc = desc.partition("\n")[0] + "...\n"
		    
		info += change + " by " + user + ": " + desc
		
	    
	    #print report into a confirm dialog
	    cmds.confirmDialog(title = "History", icon = "information", ma = "left",  message = info, button = "Close")
	    p4.disconnect()
		
	    
	    
	
	except P4Exception:
	    errorString = "The following errors were encountered:\n\n"
	    
	    for e in p4.errors:  
		errorString += e + "\n"
	    
	    cmds.confirmDialog(title = "Perforce", icon = "critical", message = errorString)
	    p4.disconnect()
	    return
	
	
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
def p4_submitCurrentFile(fileName, desc, *args):
    
        fileArg = fileName
        #try to connect
	p4 = P4()
	
	
	try:
	    p4.connect()
	    
	except:
	    cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to connect to perforce server.")
	    return
	
	#find currently opened file name
	if fileName == None:
	    fileName = cmds.file(q = True, sceneName = True)
	    
	reopen = False
	syncFiles = []
	
	
	try:
	    #client info
	    spec = p4.run( "client", "-o" )[0]
	    client = spec.get("Client")
	    owner = spec.get("Owner")	
	    p4.user = owner
	    p4.client = client    
	    
	except:
	    cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to obtain client spec information.")
	    
	    
	
	#SUBMIT
	try:
	    	    
	    if desc == None:
		result = cmds.promptDialog(title = "Perforce", message = "Please Enter a Description..", button = ["Accept", "Cancel"], defaultButton = "Accept", dismissString = "Cancel", cancelButton = "Cancel")
		
	    else:
		result = "Accept"
		
	    #process
	    if result == "Accept":
		
		#description = "test"
		myFile = p4.run_have(fileName)[0]
		depotVersion = p4.run_files(myFile['depotFile'])[0]
		depotFile = depotVersion['depotFile']
		
		#check to see if file is checked out
		opened = p4.run_opened(depotFile)
		
		if len(opened) > 0:
		    opendBy = opened[0]['user']

		    
		    if opendBy.lower() != owner.lower():
			cmds.confirmDialog(title = "Perforce", icon = "warning", message = "This file is already checked out by: " + opendBy, button = "Close")
			p4.disconnect()
			return
		
		    else:
		    
			#fetch the description
			if desc == None:
			    desc = cmds.promptDialog(q = True, text = True)	    
			
			#save the file locally (so depot and HD are in sync)
			openedFile = cmds.file(q = True, sceneName = True)
			saveFileName = openedFile.rpartition("/")[2]
			if fileArg == None:
			    cmds.file(f = True, save = True, options = "v = 0", type = "mayaBinary")
			
			
			#grab the name of the file
			fileNameWithExt = openedFile.rpartition("/")[2]
			fileName = fileNameWithExt.rpartition(".")[0]
			
			description = (desc + "\n Affected Files: " + openedFile)
			
			#create new changelist
			newChange = p4.fetch_change()
			newChange._description = description
			
			#make sure we don't add existing default changelist files.
			newChange._files = []
			
			#determine the new number so we can refetch it. 
			newChangeNum = int(p4.save_change(newChange)[0].split()[1])
	    
	
			#change changelist number
			p4.run_reopen('-c', newChangeNum, depotFile)
			
			#submit the changelist
			p4.run_submit('-c', newChangeNum)
			
			#tell the user submit was successful
			if fileArg == None:
			    cmds.confirmDialog(title = "Perforce", icon = "information", message = "Submit Operation was successful!", button = "Close")
			    
			else:
			    return True
			
		else:
		    #if the file is not checked out by the user, let them know
		    result = cmds.confirmDialog(title = "Perforce", icon = "warning", message = "File is not checked out. Unable to continue submit operation on this file:\n\n" + fileName)
		

		
	except P4Exception:
	    if fileArg == None:
		errorString = "The following errors were encountered:\n\n"
		
		for e in p4.errors:  
		    errorString += e + "\n"
		
		cmds.confirmDialog(title = "Perforce", icon = "critical", message = errorString)
		
	    p4.disconnect()
	    return False
		
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 			
def p4_addAndSubmitCurrentFile(fileName, description, *args):
    fileArg = fileName
    #try to connect
    p4 = P4()
    
    
    try:
	p4.connect()
	
    except:
	cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to connect to perforce server.")
	return
    

    #find currently opened file name
    if fileName == None:
	fileName = cmds.file(q = True, sceneName = True)
    reopen = False
    syncFiles = []
    
    
    
    try:
	#client info
	spec = p4.run( "client", "-o" )[0]
	client = spec.get("Client")
	owner = spec.get("Owner")	
	p4.user = owner
	p4.client = client
	
    except:
	cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to obtain client spec information.")
	
    
    #find currently opened file name
    proceed = False
    if fileArg == None:
	fileName = cmds.file(q = True, sceneName = True)
	
	if fileName == "":
	    cmds.confirmDialog(title = "Perforce", icon = "warning", message = "Cannot Add file to perforce as file has no name.", button = "Close")
	    p4.disconnect()
	    return
	
	else:
	    proceed = True
	    
    else:
	proceed = True
	
    
    #if the file has a filename, 
    if proceed:
	try:
	    
	    
	    clientRoot = p4.fetch_client(p4.client)._Root
	    
	    #check to make sure client root is in the client file path
	    if os.path.normpath(fileName).find(os.path.normpath(clientRoot))  == 0:
		#if it was, then get a description for the changelist
		if description == None:
		    result = cmds.promptDialog(title = "Perforce", message = "Please Enter a Description..", button = ["Accept", "Cancel"], defaultButton = "Accept", dismissString = "Cancel", cancelButton = "Cancel")
		
		else:
		    result = "Accept"
		    
		
		if result == "Accept":
		    
		    #get changelist description
		    if description == None:
			description = cmds.promptDialog(q = True, text = True)
			
		    
		    #create changelist
		    newChange = p4.fetch_change()
		    newChange._description = description
		    
		    #make sure we don't add existing default changelist files.
		    newChange._files = []
			    
		    #determine the new number so we can refetch it.
		    newChangeNum = int(p4.save_change(newChange)[0].split()[1])		    
		    
		    
		    #description = "test"
		    p4.run_add('-c', newChangeNum, fileName)
	
		    #submit the changelist
		    p4.run_submit('-c', newChangeNum)
		    
		    #tell user operation was successful
		    if fileArg == None:
			result = cmds.confirmDialog(title = "Perforce", icon = "information", message = "File has been successfully added to perforce and submitted!", button = ["Close", "Check Out File"])
			
			if result == "Close":
			    p4.disconnect()
			    return
			
			if result == "Check Out File":
			    p4_checkOutCurrentFile(fileName)
			    
		    #return operation succuessful
		    return True
			
			


		else:
		    p4.disconnect()
		    return
		
		
	    else:
		cmds.confirmDialog(title = "Perforce", icon = "warning", message = "Cannot proceed. File is not under client's root, " + clientRoot, button = "Close")
		p4.disconnect()
		return False
	    
	    
	    
	    
	except P4Exception:
	    errorString = "The following errors were encountered:\n\n"
	    
	    for e in p4.errors:  
		errorString += e + "\n"
	    
	    cmds.confirmDialog(title = "Perforce", icon = "critical", message = errorString)
	    p4.disconnect()
	    return False
	
	
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
def p4_checkForUpdates(*args):
    #try to connect
    p4 = P4()
    
    #get maya tools path
    toolsPath = cmds.internalVar(usd = True) + "mayaTools.txt"
    if os.path.exists(toolsPath):
	
	f = open(toolsPath, 'r')
	mayaToolsDir = f.readline()
	f.close()
	
    #connect to p4
    try:
	p4.connect()
	
    except:
	cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to connect to perforce server.")
	return
    
    #find currently opened file name
    clientFile = cmds.file(q = True, sceneName = True)
    reopen = False
    syncFiles = []
    
    
    try:
	#client info
	spec = p4.run( "client", "-o" )[0]
	client = spec.get("Client")
	owner = spec.get("Owner")	
	p4.user = owner
	p4.client = client

	
    except:
	cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to obtain client spec information.")
	
	
    #this will check the maya tools directory in p4 for any updates
    try:
	syncFiles = []
	clientRoot = p4.fetch_client(p4.client)._Root
	depotDirectories = []
	
	#get current project
	if os.path.exists(mayaToolsDir + "/General/Scripts/projectSettings.txt"):
	    #read to find current project
	    f = open(mayaToolsDir + "/General/Scripts/projectSettings.txt", 'r')
	    settings = cPickle.load(f)
	    f.close()
	    
	    #write out new settings
	    project = settings.get("CurrentProject")
	    
	    if os.path.exists(mayaToolsDir + "/General/Scripts/" + project + "_Project_Settings.txt"):
		#read the depot paths to sync from the project settings
		f = open(mayaToolsDir + "/General/Scripts/" + project + "_Project_Settings.txt", 'r')
		settings = cPickle.load(f)
		f.close() 
		depotDirectories = settings.get("depotPaths")		
		print depotDirectories

	
	#look at each directory inside MayaTools
	for dir in depotDirectories:
	    depotFiles = p4.run_files(dir + "...")
	    
	    for each in depotFiles:
		
		#try to compare depot to local. It is possible that there are local files not in depot, and vise versa
		try:
		    fileInfo = p4.run_files(each['depotFile'])[0]
		    depotFilePath = fileInfo['depotFile']
		    fileName = depotFilePath.rpartition("/")[2]
		    
		    #compare local files
		    localFile = p4.run_have(depotFilePath)[0]
		    localRevision = int(localFile['haveRev'])
		    depotRevision = int(fileInfo['rev'])
		    
		    if localRevision < depotRevision:
			syncFiles.append(depotFilePath)
			
		    
		except:
		    
		    try:
			#check to see if it errors out because we don't have a local version of the file
			fileInfo = p4.run_files(each['depotFile'])[0]
			depotFilePath = fileInfo['depotFile']
			fileName = depotFilePath.rpartition("/")[2]			
			localFile = p4.run_have(depotFilePath)[0]
			
		    except:
			action = each.get("action")
			if action != "delete":
			    syncFiles.append(depotFilePath)
			    
			pass
		    
		    
		
	
	#check size of syncFiles and ask user if they want to sync
	if len(syncFiles) > 0:
	    result = cmds.confirmDialog(title = "MayaTools", icon = "warning", message = "There are new updates available to the depot directories specified by your project settings.", button = ["Update", "Not Now"])
	    
	    if result == "Update":
		for file in syncFiles:
		    p4.run_sync(file)
		    
		
		cmds.confirmDialog(title = "MayaTools", icon = "information", message = "Tools are now up to date!", button = "Close")
		p4.disconnect()
		
	    else:
		p4.disconnect()
		return
		    

    #handle any errors
    except P4Exception:
	errorString = "The following errors were encountered:\n\n"
	
	for e in p4.errors:  
	    errorString += e + "\n"
	
	cmds.confirmDialog(title = "Perforce", icon = "critical", message = errorString)
	p4.disconnect()
	return
    
    

   
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
def createNewProject(*args):
    
    if cmds.window("createNewARTProject_Window", exists = True):
	cmds.deleteUI("createNewARTProject_Window")
	
    #create window
    window = cmds.window("createNewARTProject_Window", w = 400, h = 600, mnb = False, mxb = False, title = "Create New Project")
    
    #frameLayouts for settings: Perforce/Auto-Sync
    mainLayout = cmds.columnLayout(w = 400, h = 600)
    
    #project name field
    projectName = cmds.textFieldGrp("newARTProjectNameField", label = "Project Name: ", w = 400, h = 30, parent = mainLayout, cal = [1, "left"])
    
    
    scrollLayout = cmds.scrollLayout(w = 400, h = 520, parent = mainLayout)
    columnLayout = cmds.columnLayout(w = 380, parent = scrollLayout)
    

    
    #perforce/auto-sync layout
    p4Frame = cmds.frameLayout(parent = columnLayout, w = 370, cll = True, label='Perforce/Auto-Sync', borderStyle='in')
    p4Layout = cmds.columnLayout(w = 360, parent = p4Frame, co = ["both", 5], rs = 5)
    
    #create the scrollField with the information
    cmds.scrollField(parent = p4Layout, w = 350, h = 100, editable = False, wordWrap = True, text = "Add depot paths you would like the tools to check for updates on. If updates are found, you will be notified, and asked if you would like to sync. Valid depot paths look like:\n\n//depot/usr/jeremy_ernst/MayaTools")
    
    #crete the add button
    cmds.button(w = 350, label = "Add Perforce Depot Path", parent = p4Layout, c = partial(addPerforceDepotPath, p4Layout))
    
    
    
    
    #save settings button
    cmds.button(parent = mainLayout, w = 400, h = 50, label = "Save Settings and Close", c = partial(saveProjectSettings, p4Layout, False))
    
    #show window
    cmds.showWindow(window)
    
    
    
    
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
def editProject(project, *args):
    
    if cmds.window("createNewARTProject_Window", exists = True):
	cmds.deleteUI("createNewARTProject_Window")
	
    #create window
    window = cmds.window("createNewARTProject_Window", w = 400, h = 600, mnb = False, mxb = False, title = "Edit Project")
    
    #frameLayouts for settings: Perforce/Auto-Sync
    mainLayout = cmds.columnLayout(w = 400, h = 600)
    
    #project name field
    projectName = cmds.textFieldGrp("newARTProjectNameField", label = "Project Name: ", text = project, w = 400, h = 30, parent = mainLayout, cal = [1, "left"])
    
    
    scrollLayout = cmds.scrollLayout(w = 400, h = 520, parent = mainLayout)
    columnLayout = cmds.columnLayout(w = 380, parent = scrollLayout)
    

    
    #perforce/auto-sync layout
    p4Frame = cmds.frameLayout(parent = columnLayout, w = 370, cll = True, label='Perforce/Auto-Sync', borderStyle='in')
    p4Layout = cmds.columnLayout(w = 360, parent = p4Frame, co = ["both", 5], rs = 5)
    
    #create the scrollField with the information
    cmds.scrollField(parent = p4Layout, w = 350, h = 100, editable = False, wordWrap = True, text = "Add depot paths you would like the tools to check for updates on. If updates are found, you will be notified, and asked if you would like to sync. Valid depot paths look like:\n\n//depot/usr/jeremy_ernst/MayaTools")
    
    #crete the add button
    cmds.button(w = 350, label = "Add Perforce Depot Path", parent = p4Layout, c = partial(addPerforceDepotPath, p4Layout))
    
    #get maya tools path
    toolsPath = cmds.internalVar(usd = True) + "mayaTools.txt"
    if os.path.exists(toolsPath):
	
	f = open(toolsPath, 'r')
	mayaToolsDir = f.readline()
	f.close()
    
    
    #open the project settings and auto-fill in the info
    if os.path.exists(mayaToolsDir + "/General/Scripts/" + project + "_Project_Settings.txt"):
	f = open(mayaToolsDir + "/General/Scripts/" + project + "_Project_Settings.txt", 'r')
	settings = cPickle.load(f)
	f.close() 
	
	paths = settings.get("depotPaths")
	
    
	if len(paths) > 0:
	    for path in paths:
		#add the path
		field = addPerforceDepotPath(p4Layout)
		cmds.textField(field, edit = True, text = path)
	
	
	
	
	   
    
    #save settings button
    cmds.button(parent = mainLayout, w = 400, h = 50, label = "Save Settings and Close", c = partial(saveProjectSettings, p4Layout, True))
    
    #show window
    cmds.showWindow(window)
    
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
def addPerforceDepotPath(layout, *args):
    
    field = cmds.textField(docTag = "P4DepotPath", w = 350, parent = layout)
    
    #add a RMB menu to remove the field
    menu = cmds.popupMenu(parent = field, b = 3)
    cmds.menuItem(parent = menu, label = "Remove Path", c = partial(removePerforceDepotPath, field))
    
    return field
   





# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
def removePerforceDepotPath(field, *args):
    cmds.textField(field, edit = True, visible = False, h = 1)
    #cmds.deleteUI(field) This crashes maya instantly. Come ON AUTODESK
    




# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
def saveProjectSettings(perforceLayout, edit, *args):
    
    #find p4 depot path textfields
    children = cmds.columnLayout(perforceLayout, q = True, childArray = True)
    textFields = []
    for child in children:
	if child.find("textField") == 0:
	    data = cmds.textField(child, q = True, docTag = True)
	    if data == "P4DepotPath":
		textFields.append(child)
	
    
    #make sure paths are valid
    savePaths = []
    for field in textFields:
	path = cmds.textField(field, q = True, text = True)
	if path != "":
	    
	    try:
		p4 = P4()
		p4.connect()
    
		    
	    except:
		cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to connect to perforce server.")
		return
	    
	    
	    try:
		#client info
		spec = p4.run( "client", "-o" )[0]
		client = spec.get("Client")
		owner = spec.get("Owner")	
		p4.user = owner
		p4.client = client
	
		
	    except:
		cmds.confirmDialog(title = "Perforce", icon = "critical", message = "Unable to obtain client spec information.")	
    
	    
	    #now check
	    try:
		depotFiles = p4.run_files(path + "...")
		if len(depotFiles) > 0:
		    savePaths.append(path)
	    
	    #handle any errors
	    except P4Exception:
		errorString = "The following errors were encountered:\n\n"
		
		for e in p4.errors:  
		    errorString += e + "\n"
		
		cmds.confirmDialog(title = "Perforce", icon = "critical", message = errorString)
		p4.disconnect()
		return
	    
	else:
	    #see if the text field is just hidden or if it is actually blank
	    vis = cmds.textField(field, q = True, visible = True)
	    if vis == True:
		cmds.confirmDialog(title = "Error", icon = "critical", message = "Empty string not allowed as a path name. Either remove that field, or enter a correct depot path.")
		return
	    
	    
    #write out to disk
    projectName = cmds.textFieldGrp("newARTProjectNameField", q = True, text = True)
    if projectName == "":
	cmds.confirmDialog(title = "Error", icon = "critical", message = "Empty string not allowed as a project name.")
	return
    
    #save the new project file under MayaTools/General/Scripts as projName + "_Project_Settings.txt"
    toolsPath = cmds.internalVar(usd = True) + "mayaTools.txt"
    if os.path.exists(toolsPath):
	
	f = open(toolsPath, 'r')
	mayaToolsDir = f.readline()
	f.close()
	
    if edit == False:
	if os.path.exists(mayaToolsDir + "/General/Scripts/" + projectName + "_Project_Settings.txt"):
	    cmds.confirmDialog(title = "Error", icon = "critical", message = "Project already exists with that name")
	    return
    
    #save out
    f = open(mayaToolsDir + "/General/Scripts/" + projectName + "_Project_Settings.txt", 'w')


    #create a dictionary with  values
    settings = {}
    settings["depotPaths"] = savePaths
    
    
    #write our dictionary to file
    cPickle.dump(settings, f)
    f.close()
    
    
    #delete the UI
    cmds.deleteUI("createNewARTProject_Window")
    
    
    #add the project to the menu
    create = True
    items = cmds.lsUI(menuItems = True)
    for i in items:
	data = cmds.menuItem(i, q = True, docTag = True)
	if data == "P4Proj":
	    label = cmds.menuItem(i, q = True, label = True)
	    print label
	    
	    if label == projectName:
		create = False
		
		
	    
    if create:
	menuItem = cmds.menuItem(label = projectName, parent = "perforceProjectList", cl = "perforceProjectRadioMenuCollection", rb = True, docTag = "P4Proj", c = partial(setCurrentProject, projectName))
	cmds.menuItem(parent = "perforceProjectList", optionBox = True, c = partial(editProject, projectName))
    
    
    
    #open up the projectSettings.txt file and add an entry for current project
    if os.path.exists(mayaToolsDir + "/General/Scripts/projectSettings.txt"):
	f = open(mayaToolsDir + "/General/Scripts/projectSettings.txt", 'r')
	oldSettings = cPickle.load(f)
	useSourceControl = oldSettings.get("UseSourceControl")
	f.close()
	
	#write out new settings
	settings = {}
	settings["UseSourceControl"] = useSourceControl
	settings["CurrentProject"] = projectName
	
	f = open(mayaToolsDir + "/General/Scripts/projectSettings.txt", 'w')
	cPickle.dump(settings, f)
	f.close()	
	
	
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
def setCurrentProject(projectName, *args):
    #get access to maya tools path
    toolsPath = cmds.internalVar(usd = True) + "mayaTools.txt"
    if os.path.exists(toolsPath):
	
	f = open(toolsPath, 'r')
	mayaToolsDir = f.readline()
	f.close()
	
	
    #re-write settings
    if os.path.exists(mayaToolsDir + "/General/Scripts/projectSettings.txt"):
	f = open(mayaToolsDir + "/General/Scripts/projectSettings.txt", 'r')
	oldSettings = cPickle.load(f)
	useSourceControl = oldSettings.get("UseSourceControl")
	f.close()
	
	#write out new settings
	settings = {}
	settings["UseSourceControl"] = useSourceControl
	settings["CurrentProject"] = projectName
	
	f = open(mayaToolsDir + "/General/Scripts/projectSettings.txt", 'w')
	cPickle.dump(settings, f)
	f.close()	    
    
