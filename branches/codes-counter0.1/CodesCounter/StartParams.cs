using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;

namespace CodesCounter
{

	class StartParams
	{
		public string[] filters=new string[0];
		public string path="";


		private static string PATH_FLAG = "-p";
		private static string FILTER_FLAG = "-f";

		public override string ToString()
		{
			string res = "";

			res += "[Path \'" + path + "\' Filters: ";

			for (int i = 0; i < filters.Length; i++) {
				res += filters[i] + (i == filters.Length - 1 ? "]" : "|");
			}

			
			return res;
		}

		public bool checkForAcceptedExtension(string ext) {
			if (filters.Length == 0) {
				return true;
			}

			foreach (string filter in filters) {
				//Console.WriteLine(filter+" "+ext);
				if (ext == filter) return true;
			}

			return false;

		}

		public static StartParams parse(string[] args) {
			StartParams res = new StartParams();
			List<String> filters = new List<string>();
			String path = "";

			const int START = 0, PATH_FLAG_S = 1, FILTER_FLAG_S = 2, PATH_READING = 3, FILTER_READING = 4;

			string currArg;
			int state = START;
			int i = 0;
			while (i != args.Length)
			{
				currArg = args[i];

				switch (state)
				{
					case START:
						if (currArg == PATH_FLAG)
						{
							state = PATH_FLAG_S;
						}
						break;

					case PATH_FLAG_S:
						if (currArg != FILTER_FLAG)
						{
							path += currArg + " ";
						}
						else
						{
							state = FILTER_FLAG_S;
						}

						break;

					case FILTER_FLAG_S:
						filters.Add("."+currArg);
						break;
				}

				i++;
			}

			string[] fils = new string[filters.Count];
			i=0;
			foreach (string s in filters) {
				fils[i] = s;
				i++;
			}

			res.filters = fils;
			res.path=path;

			return res;
		}

	}
}
