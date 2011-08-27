using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace CodesCounter
{
	class Scanner
	{

		public static ScanInfo scan(FileConteiner file, ScanParameters param) {
			ScanInfo info = new ScanInfo();
			info.path = file.path;
			info.fileName = file.fileName;

			string[] rawLines = file.raw.Split('\n');
			string[] cuttedLines = GetContentWithoutComments(file.raw, param.getCommentRegex()).Split('\n');

			info.rawLinesCount = rawLines.Length;
			info.cuttedLinesCount = cuttedLines.Length;

			int usefullLinesCount = 0;

			foreach (string line in cuttedLines) {
				if (isUsefulLine(line, param.getUselessChars())) {
					usefullLinesCount++;
				}
			}

			info.usefullLineCount = usefullLinesCount;

			return info;// new ScanInfo();
		}

		private static string GetContentWithoutComments(string content, Regex commentRegex) {
			string res = content;
			try
			{
				res = commentRegex.Replace(res, "");
			}
			catch (Exception e) {
				return content;
			}
			return res;
		}

		private static bool isUsefulLine(String line, char[] uselessChars) {
			int scanTo = line.Length;
			for (int i = 0; i < scanTo; i++)
			{
				char c = line[i];//.ChacharAt(i);
				if (!isSpecialSymbol(c, uselessChars))
				{
					return true;
				}
			}
			return false;
		}

		private static bool isSpecialSymbol(char checkingChar, char[] uselessChars)
		{
			foreach (char symbol in uselessChars)
			{
				if (symbol == checkingChar)
				{
					return true;
				}
			}
			return false;
		}
	}
}
