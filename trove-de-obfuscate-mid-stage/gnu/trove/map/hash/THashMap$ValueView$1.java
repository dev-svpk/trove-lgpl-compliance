package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;

class THashMap$ValueView$1 extends TObjectHashIterator {
   // $FF: synthetic field
   final THashMap$ValueView this$1;

   THashMap$ValueView$1(THashMap$ValueView var1, TObjectHash var2) {
      super(var2);
      this.this$1 = var1;
   }

   protected Object objectAtIndex(int var1) {
      return THashMap$ValueView.access$0(this.this$1)._values[var1];
   }
}
