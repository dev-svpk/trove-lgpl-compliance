package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
class TLongObjectHashMap$ValueView$1 extends TLongObjectHashMap$ValueView$TLongObjectValueHashIterator {
   // $FF: synthetic field
   final TLongObjectHashMap$ValueView this$1;

   TLongObjectHashMap$ValueView$1(TLongObjectHashMap$ValueView var1, TLongObjectHashMap$ValueView var2, TLongObjectHashMap var3) {
      super(var1, var3);
      this.this$1 = var2;
   }

   protected Object objectAtIndex(int var1) {
      return TLongObjectHashMap$ValueView.access$0(this.this$1)._values[var1];
   }
}
