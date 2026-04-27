package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
class TDoubleObjectHashMap$ValueView$1 extends TDoubleObjectHashMap$ValueView$TDoubleObjectValueHashIterator {
   // $FF: synthetic field
   final TDoubleObjectHashMap$ValueView this$1;

   TDoubleObjectHashMap$ValueView$1(TDoubleObjectHashMap$ValueView var1, TDoubleObjectHashMap$ValueView var2, TDoubleObjectHashMap var3) {
      super(var1, var3);
      this.this$1 = var2;
   }

   protected Object objectAtIndex(int var1) {
      return TDoubleObjectHashMap$ValueView.access$0(this.this$1)._values[var1];
   }
}
