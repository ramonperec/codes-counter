using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using System.IO;

namespace CodesCounter
{
	class Program
	{

		static void Main(string[] args)
		{

			StartParams startArgs = StartParams.parse(args);


			if (startArgs.path.Length==0) {
				startArgs.path=Directory.GetCurrentDirectory();	
			}

			List<FileConteiner> files;
			try
			{
				files = FileFinder.Find(startArgs);
			}
			catch (Exception e) {
				Console.WriteLine("Error: "+e.Message);
				Console.Read();
				return;
			}


			Console.WriteLine("Files founded: " + files.Count);


			int totalLines = 0;
			int totalCuttedLines = 0;
			int totalCommentLines = 0;
			int totalUsefullLines = 0;


			List<ScanInfo> infos = new List<ScanInfo>();
			ScanInfo info;
			foreach (FileConteiner file in files) {
				info = Scanner.scan(file, ScanParameters.getScanParametersForExtension(file.fileType));
				infos.Add(info);

				totalLines += info.rawLinesCount;
				totalCuttedLines += info.cuttedLinesCount;
				totalUsefullLines += info.usefullLineCount;
				totalCommentLines += (info.rawLinesCount - info.cuttedLinesCount);

			}

			Console.WriteLine("Total lines: " + totalLines);
			Console.WriteLine("Total cutted lines(without comment): " + totalCuttedLines);
			Console.WriteLine("Total useful lines: " + totalUsefullLines);
			Console.WriteLine("Total commented lines: " + totalCommentLines);




			//Console.WriteLine(startArgs.ToString());

			Console.Read();

		}
	}
}
