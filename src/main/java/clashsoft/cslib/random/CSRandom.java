package clashsoft.cslib.random;

import java.util.Random;

import clashsoft.cslib.util.CSString;

/**
 * The Class CSRandom.
 * <p>
 * A class that adds several randomization utils.
 * 
 * @author Clashsoft
 */
public class CSRandom
{
	public static int nextInt(Random random, float f)
	{
		int i = (int) f;
		f -= i;
		if (random.nextFloat() < f)
		{
			i++;
		}
		return i;
	}
	
	/**
	 * Returns a random bounded int between min (inclusive) and max (exclusive),
	 * the minimum result can be min and the maximum result can be max - 1.
	 * <p>
	 * If min equals max, min will be returned
	 * <p>
	 * If min is greater than max, max is set to min + 1
	 * <p>
	 * 
	 * @param random
	 *            the random
	 * @param min
	 *            the lower bound
	 * @param max
	 *            the upper bound
	 * @return a random bounded integer
	 */
	public static int nextInt(Random random, int min, int max)
	{
		if (max < min)
		{
			max = min + 1;
		}
		if (max == min)
		{
			return min;
		}
		return min + random.nextInt(max - min + 1);
	}
	
	public static float nextFloat(Random random, float min, float max)
	{
		if (max < min)
		{
			max = min + 1;
		}
		if (max == min)
		{
			return min;
		}
		return min + random.nextInt((int) (max - min)) * random.nextFloat();
	}
	
	public static double nextDouble(Random random, double min, double max)
	{
		if (max < min)
		{
			max = min + 1;
		}
		if (max == min)
		{
			return min;
		}
		return min + random.nextInt((int) (max - min)) * random.nextDouble();
	}
	
	/**
	 * Returns true with the given chance.
	 * 
	 * @param random
	 *            the random
	 * @param chance
	 *            the chance
	 * @return true, if successful
	 */
	public static boolean chance(Random random, float chance)
	{
		if (chance < 1)
		{
			return random.nextInt((int) (1F / chance)) == 0;
		}
		return true;
	}
	
	/**
	 * Returns a new random name.
	 * 
	 * @param random
	 *            the random
	 * @param minLength
	 *            the min length
	 * @param maxLength
	 *            the max length
	 * @return the next random name
	 */
	public static String getNextRandomName(Random random, int minLength, int maxLength)
	{
		int len = nextInt(random, minLength, maxLength);
		StringBuilder buf = new StringBuilder(len);
		
		for (int i = 0; i < len; i++)
		{
			if (i == 0)
			{
				buf.append(Character.toUpperCase(CSString.nextLetter(random)));
			}
			
			int lastIndex = buf.length() - 1;
			char last = buf.charAt(lastIndex);
			char c;
			
			// Always add a consonant after a vowel
			if (CSString.isVowel(last))
			{
				c = CSString.nextConsonant(random);
			}
			else
			{
				int rnd = random.nextInt(6);
				if (rnd < 4) // Add a new consonant
				{
					c = CSString.nextConsonant(random);
					int i1 = 0;
					while (!CSString.canCharFollowChar(last, c) && i1++ <= CSString.CONSONANTS.length())
					{
						c = CSString.nextConsonant(random);
					}
					
					if (i1 > CSString.CONSONANTS.length())
					{
						c = CSString.nextVowel(random);
					}
				}
				else
				{
					c = CSString.nextVowel(random); // Add a new vowel
				}
			}
			
			buf.append(c);
		}
		
		return buf.toString();
	}
}
