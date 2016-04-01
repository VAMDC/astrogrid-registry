
Xrepeat.prototype = new Object ();
Xrepeat.prototype.constructor = Xrepeat;
Xrepeat.superclass = Object.prototype;

/**
 * constructor
 */
function Xrepeat(oAttributes) {
	this.init(oAttributes);
}

Xrepeat.prototype.init = function(oAttributes) {
	this.attributes = oAttributes;
	
	this.id = this.attributes['id']; 
	this.nodeset = this.attributes['nodeset'];
	this.model = this.attributes['model']?this.attributes['model']:0;
	
	// add the repeat to its parent model
	Xmodels[this.model].appendRepeat(this.id);
	
	this.element = document.getElementById(this.attributes['id']);	
	this.element.repeat = this;
	
	// first repeat instance serves as the template
	this.element.instanceTemplate = _getElementsByClassName('repeatinstance', this.element)[0];
	
	this.insertActions = new Array();
	this.deleteActions = new Array();
	
	this.currentIndex = 1;
	this.parent = null;
	
	// will bindings be associated to repeats?
	this.childBind = new Array();
	this.bindings = new Array();
	
	this.unrollRepeat();

}

Xrepeat.prototype.unrollRepeat = function() {

	var xPath = new Dom3Xpath();
	var xPathString = this.nodeset;
	var contextNode = xforms.childModels[this.model].instanceData.documentElement.firstChild.firstChild;
	
	var reAbsolutePath = new RegExp("^/");						
	if(reAbsolutePath.test(xPathString)) xPathString = '/instances/instance' + xPathString;
	
	var oNodeset = xPath.evaluate(xPathString, contextNode, null, 7, null);
	
	// array of all of the repeat instances in the html document (start out as one)
	var XRepeatInstances = _getElementsByClassName('repeatinstance', this.element);
	
	// insert more form controls into the html document
	if (oNodeset.snapshotLength > XRepeatInstances.length) {
		
		// if there are more nodes in the XML model than form controls, insert more form controls
		for (var i=0; i < oNodeset.snapshotLength-XRepeatInstances.length ; i++) {
			// clone the repeat instance
			var oRepeatInstance = this.element.instanceTemplate.cloneNode(true);
			
			// append the new node into HTML document
			this.element.insertBefore(oRepeatInstance, null);			
		}
		// array of all of the repeat instances
		XRepeatInstances = _getElementsByClassName('repeatinstance', this.element);
	}
	
	// put values in the form controls	
	for (var i=0; i < oNodeset.snapshotLength; i++) {
		// add values to the form controls
		// Xmodel.repeatFormControlsBindings <- array of each Xrepeat
		// Xmodel.repeatFormControlsBindings[Xrepeat.id] <- array of each repeat instance
		// Xmodel.repeatFormControlsBindings[Xrepeat.id][XrepeatInstance #] <- array of each control item
		// Xmodel.repeatFormControlsBindings[Xrepeat.id][XrepeatInstance #][#] <- array of el and nodeset
				
		xforms.childModels[this.model].repeatFormControlsBindings[this.id][i] = this._traverseFormControls(XRepeatInstances[i], oNodeset.snapshotItem(i));
	}
	
	xforms.addDefaultListeners(this.element);
}

/*
Xrepeat.prototype._traverseFormControls = function(node,contextNode) {
	// this.childModels[i].formControlsBindings=this._traverseFormControls(document.body,this.childModels[i].instanceData.documentElement.firstChild.firstChild);
	// node is an html tag
	// contextNode is an XML node in the model document
	var xPath = new Dom3Xpath();
	var _formcontrolBindings = new Array();
	var _nFormcontrolBindings = 0;
	
	for(var j=0; j < node.childNodes.length; j++) {
		switch (node.childNodes[j].nodeType) {
			// Node is an element
			case 1: 
				if (node.childNodes[j].getAttribute('xmlns')=='http://www.w3.org/2002/xforms'  && node.childNodes[j].hasAttribute('repeat'))	{
					try {
						var xPathString = node.childNodes[j].getAttribute('nodeset') || node.childNodes[j].getAttribute('ref');
						
						var obj = xPath.evaluate(xPathString, contextNode, null, 9, null);
						
						//alert(xPathString + '=' + obj.nodeSet.length);
						//var s = new XMLSerializer();
						setFormValue(node.childNodes[j], obj.toString());		
						
						_formcontrolBindings[_nFormcontrolBindings++] = {'el':node.childNodes[j],'nodeset': obj.singleNodeValue }; 					
						_formcontrolBindings.merge(this._traverseFormControls(node.childNodes[j],obj.singleNodeValue));
					} catch(err) {
						node.childNodes[j].disabled = false;
						document.dispatchEvent(Xevents['xforms-binding-exception']);
					}
				} else {
					_formcontrolBindings.merge(this._traverseFormControls(node.childNodes[j],contextNode));
				}
				
				break;
		}
	}
	return _formcontrolBindings;
}
*/

Xrepeat.prototype._traverseFormControls = function(node,contextNode) {
	// this.childModels[i].formControlsBindings=this._traverseFormControls(document.body,this.childModels[i].instanceData.documentElement.firstChild.firstChild);
	// node is an html tag
	// contextNode is an XML node in the model document
	var xPath = new Dom3Xpath();
	var _formcontrolBindings = new Array();
	var _nFormcontrolBindings = 0;
	
	for(var j=0; j < node.childNodes.length; j++) {
		switch (node.childNodes[j].nodeType) {
			// Node is an element
			case 1: 
				//if (node.childNodes[j].getAttribute('xmlns')=='http://www.w3.org/2002/xforms'  && node.childNodes[j].hasAttribute('repeat') && node.childNodes[j].hasAttribute('ref'))	{
				if (node.childNodes[j].getAttribute('xmlns')=='http://www.w3.org/2002/xforms'  && node.childNodes[j].getAttribute('repeat') && node.childNodes[j].getAttribute('ref'))	{					
					try {
						var xPathString = node.childNodes[j].getAttribute('ref');
						/*
						var reAbsolutePath = new RegExp("^/");						
						if(reAbsolutePath.test(xPathString)) xPathString = '/instances/instance' + xPathString;
						*/
						
						var obj = xPath.evaluate(xPathString, contextNode, null, 9, null);

						if (obj.singleNodeValue)
							if (obj.singleNodeValue.firstChild)
								setFormValue(node.childNodes[j], obj.singleNodeValue.firstChild.nodeValue);	// obj.toString()

						_formcontrolBindings[_nFormcontrolBindings++] = {'el':node.childNodes[j],'nodeset': obj.singleNodeValue };						
						_formcontrolBindings.merge(this._traverseFormControls(node.childNodes[j],obj.singleNodeValue));
						
					} catch(err) {
						node.childNodes[j].disabled = false;
						document.dispatchEvent(Xevents['xforms-binding-exception']);
					}
				} else {
					_formcontrolBindings.merge(this._traverseFormControls(node.childNodes[j],contextNode));
				}
				
				break;
		}
	}
	return _formcontrolBindings;
}

Xrepeat.prototype.xinsert = function (oXinsert) {
// 1) Xpath the repeat

	var xPath = new Dom3Xpath();
	var xPathString = this.nodeset;
	
	var reAbsolutePath = new RegExp("^/");	
	if(reAbsolutePath.test(xPathString)) xPathString = '/instances/instance' + xPathString;
	var contextNode = xforms.childModels[this.model].instanceData.documentElement.firstChild.firstChild;
	
	var oNodeset = xPath.evaluate(xPathString, contextNode, null, 7, null);
	
	// array of all of the repeat instances in the html document (start out as one)
	var XRepeatInstances = _getElementsByClassName('repeatinstance', this.element);	
	
// 2) Evaluate & Determine position to insert the node: 
	// at = first() | last() | index('lineset') <=> index('id of repeat') | integer
	// before | after
	// insertPosition is a one-based index
	var insertPosition = 1;
	
	if (oXinsert.position.search(/first/i) != -1 )
		insertPosition = 1;
	else if (oXinsert.position.search(/last/i) != -1 )
		insertPosition = oNodeset.snapshotLength;
	else if (oXinsert.position.search(/index/i) != -1 )
		insertPosition = this.currentIndex;
	else
		insertPosition = parseInt(oXinsert.position);
	
	if (isNaN(insertPosition)) insertPosition = oNodeset.snapshotLength;
	if (oXinsert.at == 'after') insertPosition = insertPosition +1;
	
	if (insertPosition > oNodeset.snapshotLength) insertPosition = oNodeset.snapshotLength;
	if (insertPosition < 1) insertPosition = 1;	
// 3) Insert form control at the position

	// clone the repeat instance
	var oRepeatInstance = this.element.instanceTemplate.cloneNode(true);
	
	// append the new node into HTML document: element.insertBefore(newNode, refNode)
	//var newControl = XRepeatInstances[0].parentNode.insertBefore(oRepeatInstance, XRepeatInstances[insertPosition-1]);
	var newControl = XRepeatInstances[0].parentNode.insertBefore(oRepeatInstance, null);
	
// 4) Insert xmodel node at the position
	// this clones the last xmodel node in the list; Xforms protocol says to clone the first node
	var templateNode = oNodeset.snapshotItem(oNodeset.snapshotLength-1).cloneNode(true);
	//var newNode = oNodeset.snapshotItem(0).parentNode.insertBefore(templateNode, oNodeset.snapshotItem(insertPosition-1));
	var newNode = oNodeset.snapshotItem(0).parentNode.insertBefore(templateNode, null);

// 5) Add a new repeatFormControlsBindings element
// Array.splice(index, howMany, [element1][, ..., elementN])

	// add values to the form controls
	// Xmodel.repeatFormControlsBindings[Xrepeat.id][XrepeatInstance #][#] <- array of el and nodeset
	
	// xforms.childModels[this.model].repeatFormControlsBindings[this.id].splice(insertPosition-1, 0, {'el':newControl,'nodeset': newNode });	
	xforms.childModels[this.model].repeatFormControlsBindings[this.id].splice(XRepeatInstances.length, 0, this._traverseFormControls(newControl, newNode));	
	
	// apply the default event listeners
	xforms.addDefaultListeners(newControl);
}

Xrepeat.prototype.xdelete = function (oXdelete) {

// 1) Xpath the repeat

	var xPath = new Dom3Xpath();
	
	var xPathString = this.nodeset;
	
	var reAbsolutePath = new RegExp("^/");						
	if(reAbsolutePath.test(xPathString)) xPathString = '/instances/instance' + xPathString;
	
	var contextNode = xforms.childModels[this.model].instanceData.documentElement.firstChild.firstChild;
	
	var oNodeset = xPath.evaluate(xPathString, contextNode, null, 7, null);
	
	// array of all of the repeat instances in the html document (start out as one)
	var XRepeatInstances = _getElementsByClassName('repeatinstance', this.element);

// 2) Evaluate & Determine position to delete the node: 
	// at = first() | last() | index('lineset') <=> index('id of repeat') | integer
	// deletePosition is a one-based index
	var deletePosition = 1;
	
	if (oXdelete.position.search(/first/i) != -1 )
		deletePosition = 1;
	else if (oXdelete.position.search(/last/i) != -1 )
		deletePosition = oNodeset.snapshotLength;
	else if (oXdelete.position.search(/index/i) != -1 )
		deletePosition = this.currentIndex;
	else
		deletePosition = parseInt(oXdelete.position);
	
	if (isNaN(deletePosition)) deletePosition = oNodeset.snapshotLength;	
	if (deletePosition > oNodeset.snapshotLength) deletePosition = oNodeset.snapshotLength;
	if (deletePosition < 1) deletePosition = 1;	
	
// 3) delete form control at the position
	var oldControl = XRepeatInstances[0].parentNode.removeChild(XRepeatInstances[(deletePosition-1)]);
		
// 4) delete xmodel node at the position
	var oldNode = oNodeset.snapshotItem(0).parentNode.removeChild(oNodeset.snapshotItem(deletePosition-1));

// 5) Remove repeatFormControlsBindings element
// splice(index, howMany, [element1][, ..., elementN])

	xforms.childModels[this.model].repeatFormControlsBindings[this.id].splice(deletePosition-1, 1);
	xforms.removeDefaultListeners(oldControl);
}

/**
 * insert
 */

Xinsert.prototype = new Object ();
Xinsert.prototype.constructor = Xinsert;
Xinsert.superclass = Object.prototype;

/**
 * constructor
 */
function Xinsert(oId, oAttributes) {
	document.dispatchEvent(Xevents['xforms-insert-construct']);
	
	this.attributes = oAttributes;
	
	this.nodeset = this.attributes['nodeset'];
	this.at = this.attributes['at'];
	this.position = this.attributes['position'];
	this.model = this.attributes['model']?this.attributes['model']:0;
	
	this.element = document.getElementById(oId);
	this.element.xinsert = this;
	this.element.addEventListener(this.attributes['ev:event'],Xactions.insertHandler,false);	
	
	this.parent = this.findParent();
}

Xinsert.prototype.findParent = function() {
	// loop through the array Xrepeats to find the repeat with a matching nodeset	
	for(var i=0; i < Xrepeats.length; i++) {
		if (Xrepeats[i].nodeset == this.nodeset) {
			Xrepeats[i].insertActions.push(this);
			return Xrepeats[i];
		}	
	}
	// need to create an exception
	return null;
}

Xdelete.prototype = new Object ();
Xdelete.prototype.constructor = Xdelete;
Xdelete.superclass = Object.prototype;

/**
 * constructor
 */
function Xdelete(oId, oAttributes) {
	document.dispatchEvent(Xevents['xforms-delete-construct']);
	
	this.attributes = oAttributes;
	
	this.nodeset = this.attributes['nodeset'];
	this.at = this.attributes['at'];
	this.position = this.attributes['position'];
	this.model = this.attributes['model']?this.attributes['model']:0;
	
	this.element = document.getElementById(oId);
	this.element.xdelete = this;
	this.element.addEventListener(this.attributes['ev:event'],Xactions.deleteHandler,false);	
	
	this.parent = this.findParent();
}

Xdelete.prototype.findParent = function() {
	// loop through the array Xrepeats to find the repeat with a matching nodeset	
	
	for(var i=0; i < Xrepeats.length; i++) {
		if (Xrepeats[i].nodeset == this.nodeset) {
			Xrepeats[i].deleteActions.push(this);
			return Xrepeats[i];
		}	
	}
	// need to create an exception
	return null;
}