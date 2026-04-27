package gnu.trove.map;

public interface TByteCharMap {
   char getNoEntryValue();

   char get(byte var1);

   int size();

   gnu.trove.iterator.TByteCharIterator iterator();
}
