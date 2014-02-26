/****************************
Copyright (c) 2013 Canadensys
Explorer Utilities
****************************/
/*global EXPLORER, $, window, document, console, google*/

EXPLORER.utils = (function(){

  'use strict';

  var _private = {

    //leftZeroPad for 2 digit integer only (day and month)
    //8 will return 08
    dateElementZeroPad: function(intValue) {
      if(!intValue){
        return intValue;
      }
      return String("0" + intValue).slice(-2);
    },

    //Creates a string representation for the date in the format yyyy-mm-dd.
    //Month and day will be zero padded. Month and day are optional.
    formatDate: function(year,month,day){
      var date = [];
      if(year){
        date.push(year);
      }
      if(month){
        date.push(this.dateElementZeroPad(month));
      }
      if(day){
        date.push(this.dateElementZeroPad(day));
      }
      return date.join('-');
    },

    //Tests if val is an Integer
    isInteger: function(val){
      return !isNaN(parseInt(val, 10));
    },

    createCookie: function(name,value,days) {
      var date, expires = "";

      if(value.indexOf(';') >= 0){
        console.log('Invalid cookie value. The value must not contains ; character');
        return;
      }

      if(days) {
        date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        expires = "; expires="+date.toGMTString();
      }
      document.cookie = name+"="+value+expires+"; path=/";
    },

    readCookie: function(name) {
      var nameEQ = name + "=",
          ca = document.cookie.split(';'), c;
      
      $.each(ca, function() {
        c = this;
        while(c.charAt(0) === ' ') { c = c.substring(1, c.length); }
        if (c.indexOf(nameEQ) === 0) { return c.substring(nameEQ.length,c.length); }
      });

      return null;
    },

    eraseCookie: function(name) {
      this.createCookie(name,"",-1);
    },

    getParameterByName: function(name) {
      var cname   = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]"),
          regexS  = "[\\?&]" + cname + "=([^&#]*)",
          regex   = new RegExp(regexS),
          results = regex.exec(window.location.href);

      if(results === null) { return ""; }
      return decodeURIComponent(results[1].replace(/\+/g, " "));
    },

    isWithinYearLimit: function(year){
      return (year >0 && year < 9999);
    },

    isWithinMonthLimit: function(month){
      return (month >=1 && month <=12);
    },

    isWithinDayLimit: function(day){
      return (day >=1 && day <=31);
    },

    isValidNumber: function(number){
      if(isNaN(number)){
        return false;
      }
      return true;
    },

    isValidEmail: function(email){
      return (/^\S+@\S+\.\S+$/i).test(email);
    },

    isValidDateElement: function($el){
      var val = $el.val();
      //set the base of parseInt to 10 to accept int like 08
      var valAsInt = parseInt(val,10);
      //make sure it's a number
      if(isNaN(valAsInt)){
        return false;
      }

      if($el.hasClass('validationYear')){
        return this.isWithinYearLimit(valAsInt);
      }
      if($el.hasClass('validationMonth')){
        return this.isWithinMonthLimit(valAsInt);
      }
      if($el.hasClass('validationDay')){
        return this.isWithinDayLimit(valAsInt);
      }
    },

    isValidPartialDate: function(year,month,day){
      //set the base of parseInt to 10 to accept int like 08
      var yAsInt = parseInt(year,10);
      var mAsInt = parseInt(month,10);
      var dAsInt = parseInt(day,10);

      //accept all partial dates for year alone and year/month if the day is not specified
      if(!isNaN(yAsInt) && isNaN(dAsInt)){
        return this.isWithinYearLimit(yAsInt) && (isNaN(mAsInt) || this.isWithinMonthLimit(mAsInt));
      }

      //accept all partial dates for month/day
      if(isNaN(yAsInt) && !isNaN(mAsInt) && !isNaN(dAsInt)){
        return this.isWithinMonthLimit(mAsInt) && this.isWithinDayLimit(dAsInt);
      }

      //accept all partial dates for month alone
      if(isNaN(yAsInt) && !isNaN(mAsInt) && isNaN(dAsInt)){
        return this.isWithinMonthLimit(mAsInt);
      }

      //accept all partial dates for day alone
      if(isNaN(yAsInt) && isNaN(mAsInt) && !isNaN(dAsInt)){
        return this.isWithinDayLimit(dAsInt);
      }

      if(!isNaN(yAsInt) && !isNaN(mAsInt) && !isNaN(dAsInt)){
        // day 0 means the last day/hour of the previous month, Date is 0 based so we ask for the month later
        var date = new Date(yAsInt, mAsInt,0);
        if(dAsInt > date.getDate()){
          return false;
        }
        return this.isWithinYearLimit(yAsInt) && this.isWithinMonthLimit(mAsInt) && this.isWithinDayLimit(dAsInt);
      }
      return false;
    },

    isValidDateInterval: function(syear,smonth,sday,eyear,emonth,eday){
      return (isNaN(parseInt(syear, 10)) === isNaN(parseInt(eyear, 10)) &&
          isNaN(parseInt(smonth, 10)) === isNaN(parseInt(emonth, 10)) &&
          isNaN(parseInt(sday, 10)) === isNaN(parseInt(eday, 10)));
    }

  };
  
  return {
    init: function() { return; },
    dateElementZeroPad: function(a) { _private.dateElementZeroPad(a); },
    formatDate: function(a,b,c) { _private.formatDate(a,b,c); },
    isInteger: function(a) { _private.isInteger(a); },
    createCookie: function(a,b,c) { _private.createCookie(a,b,c); },
    readCookie: function(a) { _private.readCookie(a); },
    getParameterByName: function(a) { _private.getParameterByName(a); },
    isWithinYearLimit: function(a) { _private.isWithinYearLimit(a); },
    isWithinMonthLimit: function(a) { _private.isWithinMonthLimit(a); },
    isWithinDayLimit: function(a) { _private.isWithinDayLimit(a); },
    isValidNumber: function(a) { _private.isValidNumber(a); },
    isValidEmail: function(a) { _private.isValidEmail(a); },
    isValidDateElement: function(a) { _private.isValidDateElement(a); },
    isValidPartialDate: function(a) { _private.isValidPartialDate(a); },
    isValidDateInterval: function(a,b,c,d,e,f) { _private.isValidDateInterval(a,b,c,d,e,f); }
  };

}());