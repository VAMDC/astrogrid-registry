regexps=new Array();
// xforms 1.0 datatypes primitives
regexps['xsd:string']= new Xdatatype('\\w+');
regexps['xsd:boolean']= new Xdatatype('^true|false|0|1$');

regexps['xsd:integer']= new Xdatatype('^[0-9]*$');

regexps['xsd:nonPositiveInteger']= new Xdatatype('^[-][0-9]*$');
regexps['xsd:nonNegativeInteger']= new Xdatatype('^[+]?[0-9]*$');

regexps['xsd:negativeInteger']= new Xdatatype('^[-][0-9]*$');
regexps['xsd:positiveInteger']= new Xdatatype('^[+]?[0-9]*$');

regexps['xsd:decimal']= new Xdatatype('^[-+]?[0-9]*\\.[0-9]*$');

regexps['xsd:anyURI']= new Xdatatype('^[a-z]+://([a-z0-9]*:[a-z0-9]*@)?([a-z0-9.]*\.[a-z]{2,7})([\\/\\w\\.]+)?(\\?[\\w%&=]+)?(#\\w+)?$');	

regexps['xsd:date']= new Xdatatype('^([12][0-9]{3})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])(Z|[+-]([01][0-9]|2[0-3]):[0-5][0-9])?$');
regexps['xsd:time']= new Xdatatype('^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](\.[0-9]+)?(Z|[+-]([01][0-9]|2[0-3]):[0-5][0-9])?$');
regexps['xsd:datetime']= new Xdatatype('^([12][0-9]{3})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])T([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](\.[0-9]+)?(Z|[+-]([01][0-9]|2[0-3]):[0-5][0-9])?$');
regexps['xsd:duration']= new Xdatatype('^P([0-9]+Y)?([0-9]+M)?([0-9]+D)?(T([0-9]+H)?([0-9]+M)?([0-9]+S)?)?$');
regexps['xsd:gDay'] = new Xdatatype('^(0[1-9]|[12][0-9]|3[01])$');
regexps['xsd:gMonth'] = new Xdatatype('^(0[1-9]|1[012])$');
regexps['xsd:gMonthDay'] = new Xdatatype('^(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$');
regexps['xsd:gYear'] = new Xdatatype('^([12][0-9]{3})$');
regexps['xsd:gYearMonth'] = new Xdatatype('^([12][0-9]{3})-(0[1-9]|1[012])$');

/*
regexps['xsd:base64Binary'] = new Xdatatype('');
regexps['xsd:hexBinary'] = new Xdatatype('');
regexps['xsd:float'] = new Xdatatype('');
regexps['xsd:double']= new Xdatatype('');
*/

// xforms 1.1 datatypes primitives
regexps['xsd:email']= new Xdatatype('^[\\w-]+(?:\\.[\\w-]+)*@(?:[\\w-]+\\.)+[a-zA-Z]{2,7}$');	


// xforms unofficial datatypes primitives
//([\\/\\w\\.]+)?(\\?[\\w%&=]+)?(#\\w+)?
regexps['xsd:url']= new Xdatatype('^(ht|ft)tp(s?)://([a-z0-9]*:[a-z0-9]*@)?([a-z0-9.]*\.[a-z]{2,7})$');	
regexps['xsd:frenchSocialSecurityNumber']= new Xdatatype('^((\\d(\\x20)\\d{2}(\\x20)\\d{2}(\\x20)\\d{2}(\\x20)\\d{3}(\\x20)\\d{3}((\\x20)\\d{2}|))|(\\d\\d{2}\\d{2}\\d{2}\\d{3}\\d{3}(\\d{2}|)))$',frenchSocialSecurityNumberVerify);	

regexps['xsd:creditcardExpiry'] = new Xdatatype('^((0[1-9])|(1[0-2]))\\/(\\d{2})$');
regexps['xsd:money'] = new Xdatatype('^((0[1-9])|(1[0-2]))\\/(\\d{2})$');

regexps['xsd:postcodeFr'] = new Xdatatype('^[0-9]{5}$',postcodeFrVerify);

regexps['xsd:siret'] = new Xdatatype('^[0-9]{14}$',siretVerify);
regexps['xsd:siren'] = new Xdatatype('^[0-9]{9}$',sirenVerify);

regexps['xsd:creditcardNumber'] = new Xdatatype('^((?:4\\d{3})|(?:5[1-5]\\d{2})|(?:6011)|(?:3[68]\\d{2})|(?:30[012345]\\d))[ -]?(\\d{4})[ -]?(\\d{4})[ -]?(\\d{4}|3[4,7]\\d{13})$',luhnVerify);

regexps['xsd:macAddress'] = new Xdatatype(' ^([0-9a-fA-F][0-9a-fA-F]:){5}([0-9a-fA-F][0-9a-fA-F])$');
regexps['xsd:ipAddress'] = new Xdatatype('^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$');
regexps['xsd:fqdn'] = new Xdatatype('^[a-z0-9.]*\.[a-z]{2,7}$',nsLookup);

function Xdatatype(oRegExp) {
	this.regExp = new RegExp(oRegExp);
	this.verifyHandler = dummy;
	
	if (arguments.length>1) {
		this.verifyHandler = arguments[1];
		//alert(this.regExp+'\n'+this.verifyHandler);
	}
}


Xdatatype.prototype.match = function(oStr) {
	this.matches = null;
	if (this.matches=this.regExp.exec(oStr) && this.verifyHandler(oStr)) {
	/*
	var s = "Match at position " + matches.index + ":\n";
    for (i = 0; i < matches.length; i++) s = s + matches[i] + "\n";
    alert(s);
    */
    return true;
	}
	
}


function dummy(oVal) {
	return true;	
}


function ipAddressVerify(oVal) {
	return true;	
}

function macAddressVerify(oVal) {
	return true;	
}

function frenchSocialSecurityNumberVerify(oVal) {
	 
	/*97 - oVal%97
	A : sexe (1 pour un homme, 2 pour une femme)
	B : année de naissance
	C : mois de naissance
	D : département de naissance
	E : code de la commune de naissance
	F : rang de naissance dans le mois dans la commune
	*/
	
	return true;	
}

function nsLookup() {
	return true;
}


function postcodeFrVerify() {
	return true;
}

function sirenVerify(siren) {

    var somme = 0;
    var tmp;
    for (var cpt = 0; cpt<siren.length; cpt++) {
     if ((cpt % 2) == 1) { // Les positions paires : 2ème, 4ème, 6ème et 8ème chiffre
        	tmp = siren.charAt(cpt) * 2; // On le multiplie par 2
        	if (tmp > 9) 
        	tmp -= 9;  // Si le résultat est supérieur à 9, on lui soustrait 9
        } else {
        	tmp = siren.charAt(cpt);
    	}
        somme += parseInt(tmp);
     }

     if ((somme % 10) == 0)
        return true;  // Si la somme est un multiple de 10 alors le SIREN est valide 
      else
        return false;
    
}	

function siretVerify(siret) {
      // Donc le SIRET est un numérique à 14 chiffres
      // Les 9 premiers chiffres sont ceux du SIREN (ou RCS), les 4 suivants
      // correspondent au numéro d'établissement
      // et enfin le dernier chiffre est une clef de LUHN. 
      var somme = 0;
      var tmp;
      for (var cpt = 0; cpt<siret.length; cpt++) {
        if ((cpt % 2) == 0) { // Les positions impaires : 1er, 3è, 5è, etc... 
          tmp = siret.charAt(cpt) * 2; // On le multiplie par 2
          if (tmp > 9) 
            tmp -= 9;  // Si le résultat est supérieur à 9, on lui soustrait 9
       }
       else
         tmp = siret.charAt(cpt);
         somme += parseInt(tmp);
      }
      
      if ((somme % 10) == 0)
        return true; // Si la somme est un multiple de 10 alors le SIRET est valide 
      else
        return false;
}


function luhnVerify(oVal) {
  var numero = oVal
  var somme=0;
  var dnum=0;
  var test=0;

  for ( i = numero.length; i >= 1 ;  i--) {
    test=test+1;
	num = numero.charAt(i-1);
    if ((test % 2) != 0)
       somme+=parseInt(num)
    else {
       dnum=parseInt(num)*2;
       if (dnum >= 10)
          somme+=1+dnum-10
       else
          somme+=dnum;
    }
   
  }
  
  if (somme % 10 != 0)
	return true;
  else 
	return true;
  
}


function nbDays(month,year) {
  switch (month) { 
  	case 1:
	case 3:
	case 5:
	case 7:
	case 8:
	case 10:
	case 12:
		return 31;
		break;
	case 4:
	case 6:
	case 9:
	case 11:
		return 30;
		break;
	case 2:
		return year%4==0?29:28;
	}
}

function dateVerify(oVal) {
  if (day<=nbjours(month,year)) return true;
}		


