Xrange = function() {
	
}

var Drag = {

	obj : null,
	dropTargets : [],

	init : function(o, minX, minY, maxX, maxY)
	{
		o.selectable = false;
		o.addEventListener('mousedown',Drag.start,false);
		o.root = o;

		if (!o.root.style.left) o.root.style.left   = "0px";
		if (!o.root.style.top) o.root.style.top    = "0px";
		

		o.minX	= minX;
		o.minY	= minY;
		o.maxX	= maxX;
		o.maxY	= maxY;

	},

	start : function(e)
	{
		
		var o = Drag.obj = this;
		var x = parseInt(o.root.style.left);
		var y = parseInt(o.root.style.top);

		o.started = true;
		o.lastMouseX	= e.clientX;
		o.lastMouseY	= e.clientY;
		
		if (o.minX!=null)	o.minMouseX	= e.clientX - x + o.minX;
		if (o.maxX!=null)	o.maxMouseX	= o.minMouseX + o.maxX - o.minX;

		if (o.minY!=null)	o.minMouseY	= e.clientY - y + o.minY;
		if (o.maxY!=null)	o.maxMouseY	= o.minMouseY + o.maxY - o.minY;

		document.addEventListener('mousemove',Drag.move,false);
		document.addEventListener('mouseup',Drag.end,false);
		window.addEventListener('resize',Drag.update, false);
		
		return false;
	},

	move : function(e)
	{
		var o = Drag.obj;

		var ey	= e.clientY;
		var ex	= e.clientX;
		var y = parseInt(o.root.style.top);
		var x = parseInt(o.root.style.left);
		var nx, ny;

		if (o.minX!=null) ex = Math.max(ex, o.minMouseX);
		if (o.maxX!=null) ex = Math.min(ex, o.maxMouseX);
		if (o.minY!=null) ey = Math.max(ey, o.minMouseY);
		if (o.maxY!=null) ey = Math.min(ey, o.maxMouseY);
		
		nx = x + ex - o.lastMouseX;
		ny = y + ey - o.lastMouseY;

		Drag.obj.root.style.left = nx + "px";
		Drag.obj.root.style.top = ny + "px";
		Drag.obj.lastMouseX	= ex;
		Drag.obj.lastMouseY	= ey;

		return false;
	},

	over: function(e) {
		for(var i=0; i < Drag.dropTargets.length; i++) {
				//Drag.obj.dropTargets[i];
		}
	},

	end : function(e)
	{
		document.removeEventListener('mousemove',Drag.move,false);
		document.removeEventListener('mouseup',Drag.end,false);
		document.removeEventListener('mouseover',Drag.over,false);
		Drag.obj = null;
	},

	addDropTarget : function(el) {
		this.dropTargets[this.dropTargets.length] = new DropTarget(el, this);
	},

	update : function (e) {
		//alert('update drag');
	}
};

DropTarget = function(el, dragObj){
	this.el = el;
	this.el.addEventListener('mouseover',this.over,false);
};

function Slider(oElement,oAttributes) {
	this._blockIncrement = 10;
	this._step = oAttributes['step'];
	this._minimum= oAttributes['start'];
	this._maximum= oAttributes['end'];
	this._value=0;
	
	this.element = document.getElementById(oElement);
	
	this.element.slider = this;
	this.element.unselectable = "on";	
	this.element.className='horizontal';
	this.line = document.createElement('div');
	this.line.appendChild(document.createElement("DIV"));
	this.line.className='line';

	this.handle = document.createElement('div');
	this.handle.className='handle';
	
	this.element.appendChild(this.line);	
	this.element.appendChild(this.handle);	

	var w = this.element.offsetWidth;
	var h = this.element.offsetHeight;
	
	var hw = this.handle.offsetWidth;
	var hh = this.handle.offsetHeight;
	var lw = this.line.offsetWidth;
	var lh = this.line.offsetHeight;
	
	this.handle.style.left = (w - hw) * (this._value - this._minimum) /(this._maximum - this._minimum) + "px";
	
	this.handle.style.top = Math.round((h - hh) / 2+0.5) + "px";
	this.line.style.top = (h - lh) / 2 + "px";

	this.line.style.left = hw / 2 + "px";
	this.line.style.width = Math.max(0, w - hw - 2)+ "px";
	this.line.firstChild.style.width = Math.max(0, w - hw - 4)+ "px";

	this.handle.id = 'handle';
	Drag.init(this.handle,0,parseInt(this.handle.style.top),parseInt(this.line.style.width)+2,parseInt(this.handle.style.top));

	document.addEventListener('mousemove',handler,false);	
	
	function handler(evt) {
		if (Drag.obj) Drag.obj.parentNode.slider.recalculate();
	}	
}

Slider.prototype.getValue = function () {
	return this._value;	
}

Slider.prototype.getMinimum = function () {
	return this._minimum;	
}

Slider.prototype.getMaximum = function () {
	return this._maximum;	
}

Slider.prototype.setValue = function (oVal) {
	var w = this.element.offsetWidth;
	var hw = this.handle.offsetWidth;

	this._value = oVal;
	this.handle.style.left = (w - hw) * (this._value - this._minimum) /(this._maximum - this._minimum) + "px";

}

Slider.prototype.recalculate = function () {
	
	var w = this.element.offsetWidth;
	var hw = this.handle.offsetWidth;

	this._value =  parseInt(this.handle.style.left) / (w - hw) * (this._maximum - this._minimum) + this._minimum;
	this._value =  Math.round(this._value / this._step,0)*this._step;
	//window.status= this._value;
	
}
