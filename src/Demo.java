import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Demo
{
	public static void main(String[] args)
	{
		List<Integer> idList = new ArrayList<Integer>();
		idList.add(101);
		idList.add(102);
		idList.add(103);
		idList.add(104);
		List<Integer> questionList = new ArrayList<Integer>();
		questionList.add(1);
		questionList.add(2);
		questionList.add(3);
		questionList.add(4);
		Iterator<Integer> idListIter = idList.iterator();
		Iterator<Integer> questionListIter = questionList.iterator();
		
		int insertIndex = 1;
		idList.add(insertIndex, 105);
		questionList.add(insertIndex, 2);
		
		idListIter = idList.listIterator();
		questionListIter = questionList.listIterator();

		int maxIndex = idList.size();
		for(int index = insertIndex + 1; index < maxIndex; index++)
		{
			questionList.set(index, questionList.get(index) + 1);
		}
		
		idListIter = idList.listIterator();
		questionListIter = questionList.listIterator();
		while(idListIter.hasNext())
		{
			int id = idListIter.next();
			int number = questionListIter.next();
			System.out.println("Question " + id + " is number " + number);
		}
	}
}
