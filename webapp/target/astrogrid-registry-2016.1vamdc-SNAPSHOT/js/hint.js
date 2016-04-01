function Hint(oElement,oHtml) {
	this.element = oElement;
	this.element.hint = this;
	this.html = oHtml;
		
	this.element.addEventListener('mouseover',this.show,false);
	this.element.addEventListener('mouseout',this.hide,false);
	this.element.addEventListener('click',this.hide,false);

	this.tip = document.createElement("DIV");
	this.tip.style.visibility = "hidden";
	this.tip.className = "help-tooltip";
	document.body.appendChild(this.tip);		
	this.tip.innerHTML = this.html;
} 

Hint.prototype.show  = function (evt) {
	if (evt.target.hint==null) return;
	//document.dispatchEvent(Xevents['xforms-hint']);
	var o = evt.target.hint.tip;	
	evt.target.style.cursor = 'help';
	
	o.style.visibility = "visible";

		
	// width
	if (o.offsetWidth >= window.innerWidth)
		o.stylevt.width = window.innerWidth - 10 + "px";
	else
		o.style.width = "";
	
	o.style.left = evt.pageX+10 +"px";
	o.style.top = evt.pageY+10+"px";

    o.innerHTML = evt.target.hint.html;
    

}

Hint.prototype.hide  = function (evt) {
	if (evt.target.hint==null) return;
	var o = evt.target.hint.tip;	
	o.style.visibility = "hidden";
	
	
}
