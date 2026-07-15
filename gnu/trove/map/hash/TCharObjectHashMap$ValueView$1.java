package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
class TCharObjectHashMap$ValueView$1 extends TCharObjectHashMap$ValueView$TCharObjectValueHashIterator {
   // $FF: synthetic field
   final TCharObjectHashMap$ValueView this$1;

   TCharObjectHashMap$ValueView$1(TCharObjectHashMap$ValueView var1, TCharObjectHashMap$ValueView var2, TCharObjectHashMap var3) {
      super(var1, var3);
      this.this$1 = var2;
   }

   protected Object objectAtIndex(int var1) {
      return TCharObjectHashMap$ValueView.access$0(this.this$1)._values[var1];
   }
}
