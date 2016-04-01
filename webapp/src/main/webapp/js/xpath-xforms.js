// boolean functions

var xformsIf = function() {
  var c = arguments[0];
  if (arguments.length != 4) {
    throw new Error("Function Xforms if expects (string,string,string)");
  }
 
  var arg1 = arguments[1].evaluate(c).booleanValue();

  var arg2 = arguments[2].evaluate(c).stringValue();
  var arg3 = arguments[3].evaluate(c).stringValue();
  
  return new XString(arg1?arg2:arg3);

}

var xformsBooleanFromString = function() {
  var c = arguments[0];
  if (arguments.length != 2) {
    throw new Error("Function Xforms boolean-from-string expects (string)");
  }
  
  var s = arguments[1].evaluate(c).stringValue();
  return new XBoolean(s == "1" || s == "true");
	
}


// number functions

var xformsAvg = function () {
  var c = arguments[0];
  if (arguments.length != 2) {
    throw new Error("Function Xforms avg expects (nodeset)");
  }
 var arg1 = arguments[1].evaluate(c).toArray();
 var avg = 0;
 
 for (var i=0; i < arg1.length; i++) {
 	avg += Number(arg1[i].firstChild.nodeValue);
 }
 
 avg = avg / arg1.length;
 	
 return new XNumber(avg);
}


var xformsMin = function () {
  var c = arguments[0];
  if (arguments.length != 2) {
    throw new Error("Function Xforms min expects (nodeset)");
  }

	var arg1 = arguments[1].evaluate(c).toArray();
	var min = arg1[0].firstChild.nodeValue;
	
	for (var i=1; i < arg1.length; i++) {
		if (arg1[i].firstChild.nodeValue < min ) var min = arg1[i].firstChild.nodeValue;
	}
	
	return new XNumber(min);
}

var xformsMax = function () {
  var c = arguments[0];
  if (arguments.length != 2) {
    throw new Error("Function Xforms max expects (nodeset)");
  }

	arg1 = arguments[1].evaluate(c).toArray();
	var max = arg1[0].firstChild.nodeValue;
	
	for (var i=1; i < arg1.length; i++) {
		if (arg1[i].firstChild.nodeValue > max ) var max = arg1[i].firstChild.nodeValue;
	}
	
	return new XNumber(max);
}


var xformsCountNonEmpty = function () {
  var c = arguments[0];
  if (arguments.length != 2) {
    throw new Error("Function Xforms count-non-empty expects (nodeset)");
  }
 
 	arg1 = arguments[1].evaluate(c).toArray();
	var nonempty = 0;
	
	for (var i=0; i < arg1.length; i++) {
		if (arg1[i].firstChild.nodeValue.length > 0 ) nonempty++;
	}

	return new XNumber(nonempty);
}

var xformsIndex = function () {
  var c = arguments[0];
  if (arguments.length != 2) {
    throw new Error("Function Xforms property expects (string)");
  }
	
	return new XNumber(0);
}
  
//date and time functions

var xformsNow = function () {
	
	var dt = new Datetime();
	return new XString(dt.now());
}

var xformsSeconds = function () {
  var c = arguments[0];
  if (arguments.length != 2) {
    throw new Error("Function Xforms seconds expects (string)");
  }
	
	
	return new XNumber(0);
}


var xformsMonths = function () {
  var c = arguments[0];
  if (arguments.length != 2) {
    throw new Error("Function Xforms months expects (string)");
  }
	
	
	return new XNumber(0);
}


var xformsDaysFromDate = function () {
  var c = arguments[0];
  if (arguments.length != 2) {
    throw new Error("Function Xforms days-from-date expects (string)");
  }

	
	return new XNumber(0);
}

var xformsSecondsFromDateTime = function () {
  var c = arguments[0];
  if (arguments.length != 2) {
    throw new Error("Function Xforms seconds-from-date expects (string)");
  }
	
	return new XNumber(0);
}

// string functions

var xformsProperty = function () {
  var c = arguments[0];
  if (arguments.length != 2) {
    throw new Error("Function Xforms property expects (string)");
  }
  
  arg1 = arguments[1].evaluate(c).stringValue();
  
  switch (arg1) {
  	case 'version': 
  	  return new XString('1.0');
  	case 'conformance-level':
  	  return new XString('full');
  }
  
}


// nodeset functions
var xformsInstance = function () {
   var c = arguments[0];
  if (arguments.length != 2) {
    throw new Error("Function Xforms instance expects (string)");
  }
	arg1= arguments[1].evaluate(c).stringValue();
	expression = "/instances/instance[@id='"+arg1+"']/*[1]";
	
	parser = new XPathParser();
	xpath = parser.parse(expression);

	return 	xpath.evaluate(c);
}

var xformsCurrent = function () {
  var c = arguments[0];
  if (arguments.length != 1) {
    throw new Error("Function Xforms current no requires any parameters");
  }
	
	return new XNodeSet();
}


var serializeXML = function () {
  var c = arguments[0];
  var s = new XMLSerializer();

  if (arguments.length != 2) {
    throw new Error("Function Serialize expects (node)");
  }

  arg1= arguments[1].evaluate(c).toArray()[0];	
   		
  return new XString(s.serializeToString(arg1));
}

var serializeHTML = function () {
  var c = arguments[0];
  var s = new XMLSerializer();	
  
  if (arguments.length != 2) {
    throw new Error("Function SerializeHTML expects (node)");
  }
   
  arg1= arguments[1].evaluate(c).toArray()[0];	
  
  
  return new XString(s.serializeToHtml(arg1));
}



var funRes = new FunctionResolver();

funRes.addFunction("","if", xformsIf);
funRes.addFunction("","boolean-from-string", xformsBooleanFromString);

funRes.addFunction("","avg", xformsAvg);
funRes.addFunction("","min", xformsMin);
funRes.addFunction("","max", xformsMax);
funRes.addFunction("","count-non-empty", xformsCountNonEmpty);
funRes.addFunction("","index", xformsIndex);

funRes.addFunction("","now", xformsNow);
funRes.addFunction("","seconds", xformsSeconds);
funRes.addFunction("","months", xformsMonths);
funRes.addFunction("","days-from-date", xformsDaysFromDate);
funRes.addFunction("","seconds-from-dateTime", xformsSecondsFromDateTime);

funRes.addFunction("","property", xformsProperty);

funRes.addFunction("","instance", xformsInstance);
funRes.addFunction("","current", xformsCurrent);


funRes.addFunction("uri:internal","Serialize", 	   serializeXML);
funRes.addFunction("uri:internal","SerializeHTML", serializeHTML);