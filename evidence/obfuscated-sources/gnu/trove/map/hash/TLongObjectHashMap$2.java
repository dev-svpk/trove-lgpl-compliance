package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.Z_ref;

class TLongObjectHashMap$2 implements Z_ref {
   private boolean first;
   // $FF: synthetic field
   final TLongObjectHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TLongObjectHashMap$2(TLongObjectHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(long var1, Object var3) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(",");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var3);
      return true;
   }
}
