package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.ba.aa;
import gnu.trove.ca.a;

class THashMap$ValueView$1 extends a {
   // $FF: synthetic field
   final THashMap$ValueView this$1;

   THashMap$ValueView$1(THashMap$ValueView var1, aa var2) {
      super(var2);
      this.this$1 = var1;
   }

   protected Object objectAtIndex(int var1) {
      return THashMap$ValueView.access$0(this.this$1)._values[var1];
   }
}
