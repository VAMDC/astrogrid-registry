/**
 *  XFORMS Switch Module 
 *
 *
 */
  
/**
 *  XFswitch class
 */ 

/**
 *  XFswitch constructor
 */ 
function Xswitch() {
	this.childNodes = new Array();	
	this.nChildNodes = 0;
}

/**
 *  XFswitch appendChild
 */ 
Xswitch.prototype.appendChild = function(node) {
	this.childNodes[this.nChildNodes] = node;
	this.nChildNodes++;
}


/**
 *  XFswitch toggle
 */ 
Xswitch.prototype.toggle = function() {
	for(var i=0;i<this.childNodes.length;i++) {
		if(this.childNodes[i].selected) {
			this.childNodes[i].toggle(true);
			return;
		}
	}
	this.childNodes[0].toggle(true);
	return;
	
}

/**
 *  XFcase class
 *
 *
 */ 

/**
 *  XFcase constructor
 */ 
function Xcase(oElement,oSelected) {
	this.element = document.getElementById(oElement);
	this.selected=oSelected;
	this.element.xcase = this;
	//this.element.className='hide';
}

/**
 *  XFcase toggle
 */ 
Xcase.prototype.toggle = function(oSelected) {
	this.selected = oSelected;
	this.element.className=oSelected?'show':'hide';
}

/**
 *  XFtoggle event handler
 */ 
function Xtoggle(evt) {

	oToggle = document.getElementById(evt.currentTarget.trigger.attributes['case']);
	oParentToggle = oToggle.parentNode;

	for(var i=0;i<oParentToggle.childNodes.length;i++) {
		if (oParentToggle.childNodes[i].nodeType==1) {
			oParentToggle.childNodes[i].xcase.toggle(false);		
		}
	}
	oToggle.xcase.toggle(true);
}