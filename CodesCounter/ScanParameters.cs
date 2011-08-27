using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace CodesCounter
{
	class ScanParameters
	{
		private static ScanParameters C_PLUS_PLUS;
		private static ScanParameters TEXT;

		/// <summary>
		/// Ну это предстоит сделать Вам =)
		/// </summary>
		private static ScanParameters XML;

		private static Dictionary<string, ScanParameters> extenstionToNotation = initETNDictionary();

		private static Dictionary<string, ScanParameters> initETNDictionary()
		{
			Dictionary<string, ScanParameters> res = new Dictionary<string, ScanParameters>();

			C_PLUS_PLUS = new ScanParameters();
			C_PLUS_PLUS.setUselessChars(new char[] { '\t', ' ', '\n', '{', '}', '(', ')', ';', '\r' });
			C_PLUS_PLUS.setCommentRegex("(/\\*[^\\*]*[^/]*/|\\/\\/[^\\n]*)");

			TEXT = new ScanParameters();
			TEXT.setUselessChars(new char[]{});

			res.Add("*", TEXT);

			res.Add(".java", C_PLUS_PLUS);
			res.Add(".cs", C_PLUS_PLUS);
			res.Add(".as", C_PLUS_PLUS);
			res.Add(".cpp", C_PLUS_PLUS);
			res.Add(".h", C_PLUS_PLUS);
			res.Add(".c", C_PLUS_PLUS);



			return res;
		}

		public static ScanParameters getScanParametersForExtension(string ext) {
			ScanParameters p;
			//Console.WriteLine(">>>"+ext);
			if(extenstionToNotation.ContainsKey(ext)){
				p = extenstionToNotation[ext];
			}else{
				p = extenstionToNotation["*"];
			}
			return p;
		}

		private char[] uselessSymbols;
		private Regex commentRegExp;

		public void setUselessChars(char[] chars) {
			uselessSymbols = chars;
		}

		public void setCommentRegex(string regexString) {
			commentRegExp = new Regex(regexString);
		}

		public char[] getUselessChars() {
			return uselessSymbols;
		}

		public Regex getCommentRegex() {
			return commentRegExp;
		}




	}
}
