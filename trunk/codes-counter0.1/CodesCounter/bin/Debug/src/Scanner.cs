using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CodesStringCounter
{
	class Scanner
	{
		public char[] specialSymbols;

    public void setSpecialSymbols(char[] specialSymbols)
    {
        this.specialSymbols = specialSymbols;
    }

    public bool isLineFit(String line)
    {
		
        int scanTo = line.Length;
        for (int i = 0; i < scanTo; i++)
        {
            char c = line[i];//.ChacharAt(i);
            if (!isSpecialSymbol(c))
            {
                return true;
            }
        }

        return false;
    }

    private bool isSpecialSymbol(char c)
    {
		//if()
			//Debug.trace();
        foreach (char symbol in  specialSymbols)
		{
            if (symbol == c)
			{
                return true;
			}
		}
        return false;
    }
	}
}
