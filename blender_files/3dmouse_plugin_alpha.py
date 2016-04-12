import threading
import mathutils
import time
import serial
import bpy
from bpy_extras import view3d_utils
from bpy.props import *

#https://www.blender.org/api/blender_python_api_2_63_17/bpy.ops.html
#CmdActions.parse("rotate_0_0.1")
        #bpy.ops.transform.rotate("INVOKE_DEFAULT")
        #bpy.ops.transform.rotate("INVOKE_DEFAULT",axis=(0,0,1))
        #bpy.ops.transform.translate("INVOKE_DEFAULT",constraint_axis=(False, False, True))
        #bpy.ops.transform.resize("INVOKE_DEFAULT",constraint_axis=(False, False, True))
        #
        #eval('bpy.ops.transform.rotate("INVOKE_DEFAULT",axis=(0,0,1))')
        

#TODO : See if can find a better way to hold this data then save it to the scene.
def initSceneProperties(scn):
    bpy.types.Scene.strSerialPort = StringProperty(name = "Port",default="COM3",description="Enter the com port being used for experiement")   
    bpy.types.Scene.intSerialBaud = IntProperty(name = "Baud",default=9600,description = "Enter the baud rate in which the serial port is running on")
    return

initSceneProperties(bpy.context.scene)


#====================================================================================
#Main Panel for serial settings and buttons to control connection.
class SerialMouseSettingsPanel(bpy.types.Panel):
    bl_category = "SMouse"
    bl_label = "Settings"
    bl_space_type = 'VIEW_3D' #Create in the 3D View
    bl_region_type = 'TOOLS' #Put it in the Tools Panel
    
    def draw(self, context):
        layout = self.layout
        scn = context.scene
        layout.prop(scn,"strSerialPort")
        layout.prop(scn,"intSerialBaud")
        layout.operator("serialmouse.connection",text="Connect").state = True
        layout.operator("serialmouse.connection",text="Disconnect").state = False


#====================================================================================
#single operator to connect the state of com connection               
class SerialMouse_Connection(bpy.types.Operator):
    bl_idname = "serialmouse.connection"
    bl_label = "Toggle Connection"
    state = BoolProperty(default=True)
    def execute(self,context):
        #CmdActions.parse("pan~0~1")
        #AssembleOverrideContextForView3dOps() 
        if self.state:
            scn = context.scene
            #bpy.ops.serialmouse.listener()
			#bpy.ops.serial.modal_listener()
            SerialListener.Connect(scn["strSerialPort"],scn["intSerialBaud"],context)
        else:
            #SerialListenerState.Disconnect()
            SerialListener.Disconnect()
        return{'FINISHED'}

#====================================================================================
class CmdActions:
    def parse(txt):
        ary = txt.split("~")
        if len(ary) == 0:
            return
        cmdList[ary[0]](ary)
        
    def pan(ary):
        #get reference to all the areas
        area = bpy.context.window_manager.windows[0].screen.areas[1]
        viewport = area.regions[4]
        rv3d = area.spaces[0].region_3d
        
        #convert view location's 3D Cords to 2D Cords
        locCord = rv3d.view_location
        cord =  view3d_utils.location_3d_to_region_2d(viewport, rv3d, locCord)
        
        cord[0] += float(ary[1])
        cord[1] += float(ary[2])
        
        #convert 2d cords to 3d Cords and apply
        vec = view3d_utils.region_2d_to_vector_3d(viewport, rv3d, cord)
        loc = view3d_utils.region_2d_to_location_3d(viewport, rv3d, cord, vec)
        rv3d.view_location = loc
        
    def zoom(ary):
        rv3d = bpy.context.window_manager.windows[0].screen.areas[1].spaces[0].region_3d
        rv3d.view_distance += float(ary[1])
        
    def pyaw(ary):
        rv3d = bpy.context.window_manager.windows[0].screen.areas[1].spaces[0].region_3d
        rv3d.view_rotation.rotate(mathutils.Euler(( float(ary[2]) , 0 , float(ary[1]) ))) #pitch roll 

        #yaw = ob.rotation_euler.z
        #pitch = ob.rotation_euler.y
        #roll = ob.rotation_euler.x
        
    def roll(ary):
        rv3d = bpy.context.window_manager.windows[0].screen.areas[1].spaces[0].region_3d
        rv3d.view_rotation.rotate(mathutils.Euler(( 0 , float(ary[1]) , 0 ))) #pitch roll 
    
    def exe(ary):
        SerialListenerHandler.push(ary)

cmdList = {"exe":CmdActions.exe, "roll":CmdActions.roll, "pan": CmdActions.pan, "zoom": CmdActions.zoom, "pyaw": CmdActions.pyaw }


#====================================================================================
class SerialListener:
    isActive = False
    
    def Connect(port,baud,context):
        if SerialListener.isActive:
            print("Thread is already active")
            return
        print(bpy.context.edit_object)
        SerialListener.isActive = True
        t = threading.Thread(target=SerialListener.ThreadWorker,args=(context,port,baud))
        t.start()
        bpy.ops.serialmouse.listener_handler()

    def Disconnect():
        SerialListener.isActive = False
        print("Is not Active")
    
    def ThreadWorker(contextz,port,baud):
        print("ThreadProcess Start")
        btnState = "btn_a_0"
        
        obj = serial.Serial(port,baud,timeout=0.1)                         
        buf = b''
        
        while SerialListener.isActive:
            data = obj.readline()
            if data.__len__() > 0:
                buf += data
                if b'\n' in buf:
                    tmp = buf.decode().strip(' \t\n\r')
                    buf = b''
                    CmdActions.parse(tmp)

        obj.close()    
        print("ThreadProcess End")
        SerialListener.isActive = False
        return     
		
#====================================================================================
class SerialListenerHandler(bpy.types.Operator):
	bl_idname = 'serialmouse.listener_handler'
	bl_label = 'Start Serial Mouse Handler'
	
	mTimer = None
	mQueue = []  #TODO: Find a way to get Queue Object in blender, better for passing data between threads.
	
	def modal(self, context, event):
		if SerialListener.isActive == False:
			self.cancel(context)
			return {'CANCELLED'}
		
		if event.type == 'TIMER':
			print(len(SerialListenerHandler.mQueue))
			if len(SerialListenerHandler.mQueue) != 0:
				ary = SerialListenerHandler.mQueue.pop()
				try:
					exec(ary[1])
				except(RuntimeError, TypeError, NameError):
					print("Error running " + ary[1])

		return {'PASS_THROUGH'}
	
	def execute(self, context):
		print("Starting Handler...")
		man = context.window_manager
		self.mTimer = man.event_timer_add(time_step=0.5,window=context.window)
		man.modal_handler_add(self)		
		return {'RUNNING_MODAL'}
	
	def cancel(self, context):
		context.window_manager.event_timer_remove(self.mTimer)
		print('Stopping Handler...')
		
	def push(txt):
		SerialListenerHandler.mQueue.append(txt)
		

#====================================================================================
#Register objects
def register():
    bpy.utils.register_module(__name__)
    #bpy.utils.register_class(CustomPanel)
    
def unregister():
	bpy.utils.unregister_module(__name__)
    #bpy.utils.unregister_class(SerialMouseSettingsPanel)
    

#If running as a script, do the register.
if __name__ == "__main__":
    register()