package gnu.trove.map;

public interface TIntCharMap {
   char getNoEntryValue();

   char get(int var1);

   int size();

   gnu.trove.iterator.TIntCharIterator iterator();
}
