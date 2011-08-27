using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using System.IO;
using System.Text.RegularExpressions;

namespace CodesCounter
{
	class FileConteiner
	{
		public string raw;
		public string computed;
		public string path;
		public string fileName;
		public string fileType;

		

		public FileConteiner(string path){
			StreamReader reader = new StreamReader(path);
			raw = reader.ReadToEnd();
			reader.Close();

			FileInfo fileInfo = new FileInfo(path);
			fileName = fileInfo.Name;
			fileType = fileInfo.Extension;
			this.path = path;
		}
	}
}
