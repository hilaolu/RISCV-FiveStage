package FiveStage
import chisel3._
import chisel3.util.BitPat
import chisel3.util.ListLookup

class Decoder() extends Module {

    val io = IO(new Bundle {
        val ins = Input(new Instruction)

        val ctrl_signal    = Output(new ControlSignals)
        val imm_type       = Output(UInt(3.W))
        val op_0_type      = Output(UInt(1.W))
        val op_1_type      = Output(UInt(1.W))
        val alu_op         = Output(UInt(4.W))
        val branch_type    = Output(UInt())
    })

    import lookup._
    import Op0Select._
    import Op1Select._
    import ImmFormat._
    import YN._
    import DonotCare._
    
    
    val opcodeMap: Array[(BitPat, List[UInt])] = Array(

        // signal      regWrite, memRead, memWrite, branch,  jump, branchType,Op1Select, Op2Select, ImmSelect, ALUOp
        LW     -> List(Y,        Y,       N,        N,       N,    DC,        RS1,       IMM,       ITYPE,     AluOp.ADD),
        SW     -> List(N,        N,       Y,        N,       N,    DC,        RS1,       IMM,       STYPE,     AluOp.ADD),
        NOP    -> List(N,        N,       N,        N,       N,    DC,        DC,        DC,        DC,        DC),
        
        //     
        ADD    -> List(Y,        N,       N,        N,       N,    DC,        RS1,       RS2,       DC,        AluOp.ADD),
        SUB    -> List(Y,        N,       N,        N,       N,    DC,        RS1,       RS2,       DC,        AluOp.SUB),
        ADDI   -> List(Y,        N,       N,        N,       N,    DC,        RS1,       IMM,       ITYPE,     AluOp.ADD),
    )


    val invaild = List(N, N, N, N, N, DC, DC, DC, DC, AluOp.INVAILD)

    val decodedControlSignals = ListLookup(
        io.ins.asUInt(),
        invaild,
        opcodeMap
    )
    
    when(decodedControlSignals(9)===AluOp.INVAILD){
        printf(s"INVAILD INSTRUCTION\n")
        printf("%b\n",io.ins.asUInt())
        assert(false.B)
    } 
    

    io.ctrl_signal.regWrite   := decodedControlSignals(0)
    io.ctrl_signal.memRead    := decodedControlSignals(1)
    io.ctrl_signal.memWrite   := decodedControlSignals(2)
    io.ctrl_signal.branch     := decodedControlSignals(3)
    io.ctrl_signal.jump       := decodedControlSignals(4)
    
    io.branch_type:= decodedControlSignals(5)
    io.op_0_type  := decodedControlSignals(6)
    io.op_1_type  := decodedControlSignals(7)
    io.imm_type   := decodedControlSignals(8)
    io.alu_op     := decodedControlSignals(9)
}
