package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
class TByteObjectHashMap$ValueView$1 extends TByteObjectHashMap$ValueView$TByteObjectValueHashIterator {
   // $FF: synthetic field
   final TByteObjectHashMap$ValueView this$1;

   TByteObjectHashMap$ValueView$1(TByteObjectHashMap$ValueView var1, TByteObjectHashMap$ValueView var2, TByteObjectHashMap var3) {
      super(var1, var3);
      this.this$1 = var2;
   }

   protected Object objectAtIndex(int var1) {
      return TByteObjectHashMap$ValueView.access$0(this.this$1)._values[var1];
   }
}
