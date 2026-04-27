package gnu.trove.map;

public interface TByteDoubleMap {
   double getNoEntryValue();

   double get(byte var1);

   int size();

   gnu.trove.iterator.TByteDoubleIterator iterator();
}
