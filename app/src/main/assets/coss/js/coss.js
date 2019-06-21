
function request(sp, invoke, myJSONText) {
	// 1 android 2 ios
	try {
		window.signet.transmit(sp, invoke, myJSONText);
	} catch (Excep) {
		window.location.href = "#/signet?" + invoke + ":?" + myJSONText;
	}
}

function signSettingBack() {
	request('signet', 'signSettingBack', '');
}

function setUserMobile(mobile) {
	document.getElementById("sdkActivePhoneNumber").innerHTML = mobile.substring(0, 3) + "****" + mobile.substring(7, 11);
}

function inputNumber() {
	request('signet', 'inputNumber', document.getElementById("phonenumber01").value);
}

function activeUser() {
	var otpCode = document.getElementById("otp").value;
	if (otpCode.length != 6) {
		request('signet', 'alertWarnig', '请输入六位短信验证码');
		return;
	}
	var pin1 = "";
	var pin2 = "";
	if (document.getElementById("userpin1") && document.getElementById("userpin2")) {
		pin1 = document.getElementById("userpin1").value;
		pin2 = document.getElementById("userpin2").value;

		if (pin1.length != 6 || pin2.length != 6) {
			request('signet', 'alertWarnig', '请输入6位数字口令');
			return;
		}
		if (isNaN(pin1) || isNaN(pin2)) {
			request('signet', 'alertWarnig', '请输入6位数字口令');
			return;
		}
		if (pin1 != pin2) {
			request('signet', 'alertWarnig', '口令输入不一致');
			return;
		}
	}
	var paramObj = new Object();
	paramObj.otp = otpCode;
	paramObj.pinOne = pin1;
	paramObj.pinTwo = pin2;

	request('signet', 'activeUser', JSON.stringify(paramObj));
}

//获取验证码
var sdk_time_num = 59;
var sdk_time_interval;

function sdkTimeFun() {
	$("#sdk_time").unbind();
	$("#sdk_time").html(sdk_time_num-- + " 秒");
	sdk_time_interval = setInterval(function () {
			if (sdk_time_num > 0) {
				$("#sdk_time").html(sdk_time_num-- + " 秒");
			} else {
				clearInterval(sdk_time_interval);
				$("#sdk_time").html("获取验证码");
				sdk_time_num = 59;
				$("#sdk_time").bind("click", function () {
					active_sendmsg();
				});
			}
		}, 1000);
}

function active_sendmsg() {
	request('signet', 'reactive', '');
	sdkTimeFun();
}

$(function () {
	sdkTimeFun();
});

var panduanArray = new Array(false, false, false);

function sdkPanduan(n, lengths) {
	if (((String($(".sdk_input" + n).val()).indexOf(" ") != -1)) || ((String($(".sdk_input" + n).val()).indexOf("　") != -1))) {
		$(".sdk_ts" + n).hide();
		$(".zm_sdk_ts" + n).hide();
		$(".kg_sdk_ts" + n).show();
	} else {
		$(".kg_sdk_ts" + n).hide();
		if (($(".sdk_input" + n).val().length == lengths) && (!isNaN($(".sdk_input" + n).val()))) {
			$(".sdk_ts" + n).hide();
			$(".zm_sdk_ts" + n).hide();
			panduanArray[n] = true;
		} else if ($(".sdk_input" + n).val().length == 0) {
			$(".sdk_ts" + n).hide();
			$(".zm_sdk_ts" + n).hide();
			panduanArray[n] = false;
		} else {
			if (isNaN($(".sdk_input" + n).val())) {
				$(".zm_sdk_ts" + n).show();
				$(".sdk_ts" + n).hide();
			} else {
				$(".sdk_ts" + n).show();
				$(".zm_sdk_ts" + n).hide();
			}
			panduanArray[n] = false;
		}

		if (n == 3) {
			if (panduanArray[n]) {
				$(".btn-primary").attr("disabled", false);
			} else {
				$(".btn-primary").attr("disabled", true);
			}
		} else if (n == 4) {
			if (panduanArray[n] && (String($(".sdk_input" + n).val()).charAt(0) == 1)) {
				$(".btn-primary").attr("disabled", false);
			} else {
				$(".btn-primary").attr("disabled", true);
			}
		} else {
			if (panduanArray[0] && panduanArray[1] && panduanArray[2]) {
				$(".btn-primary").attr("disabled", false);
			} else {
				$(".btn-primary").attr("disabled", true);
			}
		}
	}
}

function setTitle(title) {
	var divTitle = document.getElementById("title"); //=$("#activeTitle");
	divTitle.innerHTML = title;
}