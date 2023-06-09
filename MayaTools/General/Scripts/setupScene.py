import maya.cmds as cmds


def setupScene():
    #Setup scene to set timeline to 0-100 with current frame being 0 and the fps to be 30
    cmds.currentUnit(time = 'ntsc')
    cmds.playbackOptions(min = 0, max = 100, animationStartTime = 0, animationEndTime = 100)
    cmds.currentTime(0)
    
    
setupScene()