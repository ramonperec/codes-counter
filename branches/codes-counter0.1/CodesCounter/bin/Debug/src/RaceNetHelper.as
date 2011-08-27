package  utils
{
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.net.URLRequestMethod;
	import flash.net.URLVariables;
	import mx.controls.Alert;
	import vkontakte.VkontakteAPI;
	import vkontakte_api3.vk_auth.VkAuth;
	
	/**
	 * ...
	 * @author Shirobok Pavel aka ramshteks
	 */
	public class RaceNetHelper
	{
		private static const SERVER_URL:String = "http://www.pingpong.game-pride.ru/game";
		private static const GET_LIST:String = "/list";
		private static const CREATE_RACE:String = "/create";
		private static const ALIVE_RACE:String = "/act/alive";
		private static const KILL_RACE:String = "/act/kill";
		
		private static const SHOP_GET:String = "/shop.get";
		
		private static const REGME:String = "/regme";
		
		private static const GETRACKETS:String = "/act/getracket";
		
		private static var _loader:URLLoader;
		private static var _instance:RaceNetHelper
		
		private static var _auth:String;
		private static var _uid:String;
		private static var _errorMessage:String;
		
		public function RaceNetHelper() 
		{
			_instance = this;
			_auth = VkAuth.auth_key;//VkontakteAPI.auth_key;
			_uid = VkAuth.viewer_id;//VkontakteAPI.viewer_id;
			_loader = new URLLoader();
		}
		
		public static function regme():void {
			var req:URLRequest = getRequest();
			req.url = SERVER_URL + REGME;
			_loader.load(req);
		}
		
		public static function getRackets():void {
			var req:URLRequest = getRequest();
			req.url = SERVER_URL + GETRACKETS;
			_loader.load(req);
		}
		
		public static function getShop():void {
			var req:URLRequest = getRequest();
			req.url = SERVER_URL + SHOP_GET;
			trace(req.url);
			_loader.load(req);
		}
		
		public static function killGame(race_id:String):void {
			var req:URLRequest = getRequest();
			req.url = SERVER_URL + KILL_RACE + "/id/" + race_id;
			//trace(req.url);
			_loader.load(req);
		}
		
		public static function getList():void {
			var req:URLRequest = getRequest();
			req.url = SERVER_URL + generateRandomUrl(GET_LIST);
			trace(req.url);
			_loader.load(req);
		}
		
		public static function createRace(peer:String, type:uint,map:uint,raceClass:uint):void {
			var req:URLRequest = getRequest();
			req.url = SERVER_URL + CREATE_RACE + "/" + peer + "/" + type;
			trace(req.url);
			_loader.load(req);
		}
		
		public static function aliveRace(race_id:String, cp_count:uint):void {
			var req:URLRequest = getRequest();
			req.url = SERVER_URL + ALIVE_RACE + "/" + race_id + "/" + cp_count;
			_loader.load(req);
		}
		
		private static function generateRandomUrl(part:String):String {
			var rnd:String = Math.random().toString().substr(2);
			return part + "/" + rnd;
		}
		
		public static function get isError():Boolean {
			trace(_loader.data);
			
			Alert.show(_loader.data);
			try{
				var xml:XML = new XML(_loader.data);
			}catch (e:Error) {
				_errorMessage = "Xml is Broken:\n" + _loader.data;
				return true;
			}
			if (String(xml.code) == "") {
				return false;
			}
			_errorMessage = "Error code :: " + xml.code + " Message :: " + xml.message;
			return true;
		}
		
		
		private static function getRequest():URLRequest {
			var req:URLRequest = new URLRequest();
			req.method = URLRequestMethod.POST;
			req.data = new URLVariables("uid="+_uid+"&auth_key="+_auth);
			return req;
		}
		
		static public function get instance():RaceNetHelper { return _instance; }
		
		static public function get loader():URLLoader { return _loader; }
		
		static public function get errorMessage():String { return _errorMessage; }
		
	}

}