Debug.prototype = new Object();
Debug.prototype.constructor = Debug;
Debug.superclass = Object.prototype;

function Debug() {
		this.on = false;
		if (this.on) this.init();
}	

Debug.prototype.init = function() {
		
		this.lastTimestamp = new Date();
		this.debugDiv = document.createElement('div');
		this.debugDiv.id='debugDiv';
		
		/*this.debugDiv.innerHTML = '<div id="debugDiv">' +
								  '<h2>Debug log</h2>' +
								  '<fieldset><legend>Events</legend><div id="debugEvents"></div><button id="cleanEvents">clean</button></fieldset>' +
								  '<fieldset><legend>Expression</legend><div id="debugExp"></div><button id="cleanExp">clean</button></fieldset>' + 
								  '<fieldset><legend>General</legend><div id="debugLog"></div><button id="cleanLog">clean</button></fieldset>' +
								  '</div>';
		*/

		this.eventsWindow  = document.createElement('div');
		this.eventsWindow.id = 'debugEvents';
		this.debugDiv.appendChild(this.eventsWindow);
		
		this.logWindow = document.createElement('div');
		this.logWindow.id = 'debugLog';
		this.debugDiv.appendChild(this.logWindow);
				
		this.expWindow  = document.createElement('div');
		this.expWindow.id = 'debugExp';
		this.debugDiv.appendChild(this.expWindow);
		
		/*this.cleanEventsBtn = document.getElementById('cleanEvents');
		this.cleanEventsBtn.addEventListener('click',this.cleanHandler,false);
	
		this.cleanExpBtn = document.getElementById('cleanExp');
		this.cleanExpBtn.addEventListener('click',this.cleanHandler,false);
	
		this.cleanLogBtn = document.getElementById('cleanLog');
		this.cleanLogBtn.addEventListener('click',this.cleanHandler,false);
		*/
		
		document.body.appendChild(this.debugDiv);		

}


Debug.prototype.addMsg = function(obj,level,msg,timestamp)   {
		
		var oMsg = document.createElement('div');
		oMsg.className = level+ 'Log';
		
		var executionTime = timestamp?String((new Date(timestamp - this.lastTimestamp).getMilliseconds()) / 1000)+'ms : ':'';

		var oValue = document.createTextNode(executionTime + msg);
		oMsg.appendChild(oValue);
		
		obj.appendChild(oMsg);

		this.lastTimestamp = timestamp;
		
		this.eventsWindow.scrollTop = obj.scrollHeight-obj.clientHeight;
}

Debug.prototype.addLog = function(level,msg)   {
	if (this.on) this.addMsg(this.logWindow,level,msg);
}

Debug.prototype.addEvent = function(level,evt)   {
	if (this.on) this.addMsg(this.eventsWindow,level,evt.type,evt.timeStamp);
}


Debug.prototype.addExp = function(level,msg)   {
	if (this.on) this.addMsg(this.expWindow,level,msg);

}


Debug.prototype.cleanHandler = function(evt) {
	evt.target.parentNode.childNodes[1].innerHTML = '';
}
