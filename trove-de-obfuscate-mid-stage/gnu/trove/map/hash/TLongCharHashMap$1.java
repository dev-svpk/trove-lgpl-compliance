package gnu.trove.map.hash;

//import gnu.trove.c_ref.U_ref;
import gnu.trove.procedure.TLongCharProcedure;

class TLongCharHashMap$1 implements TLongCharProcedure {
   private boolean first;
   // $FF: synthetic field
   final TLongCharHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TLongCharHashMap$1(TLongCharHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(long var1, char var3) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var3);
      return true;
   }
}
