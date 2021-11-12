package FiveStage
import chisel3._
import chisel3.util.BitPat
import chisel3.util.ListLookup

class Decoder() extends Module {

    val io = IO(new Bundle {
        val ins = Input(new Instruction)

        val ctrl_signal    = Output(new ControlSignals)
        val imm_type       = Output(UInt(3.W))
        val op_1_type      = Output(UInt(1.W))
        val op_2_type      = Output(UInt(2.W))
        val alu_op         = Output(UInt(4.W))
        val branch_type    = Output(UInt(3.W))
    })

    import lookup._
    import Op1Select._
    import Op2Select._
    import ImmFormat._
    import YN._
    import DonotCare._
    
    
    val opcodeMap: Array[(BitPat, List[UInt])] = Array(

        // signal      regWrite, memOp, branch,  jump, branchType,Op1Select, Op2Select, ImmSelect, ALUOp
        NOP    -> List(N,        N,        N,       N,    DC,            DC,        DC,        DC,        DC),   
                
        LW     -> List(Y,        Y,        N,       N,    DC,            RS1,       IMM,       ITYPE,     AluOp.ADD),
        SW     -> List(N,        Y,        N,       N,    DC,            RS1,       IMM,       STYPE,     AluOp.ADD),     
            
        ADD    -> List(Y,        N,        N,       N,    DC,            RS1,       RS2,       DC,        AluOp.ADD),
        SUB    -> List(Y,        N,        N,       N,    DC,            RS1,       RS2,       DC,        AluOp.SUB),        
        AND    -> List(Y,        N,        N,       N,    DC,            RS1,       RS2,       DC,        AluOp.AND),
        OR     -> List(Y,        N,        N,       N,    DC,            RS1,       RS2,       DC,        AluOp.OR),
        XOR    -> List(Y,        N,        N,       N,    DC,            RS1,       RS2,       DC,        AluOp.XOR),        
        SLT    -> List(Y,        N,        N,       N,    DC,            RS1,       RS2,       DC,        AluOp.SLT),
        SLTU   -> List(Y,        N,        N,       N,    DC,            RS1,       RS2,       DC,        AluOp.SLTU),       
        SRA    -> List(Y,        N,        N,       N,    DC,            RS1,       RS2,       DC,        AluOp.SRA),
        SRL    -> List(Y,        N,        N,       N,    DC,            RS1,       RS2,       DC,        AluOp.SRL),
        SLL    -> List(Y,        N,        N,       N,    DC,            RS1,       RS2,       DC,        AluOp.SLL),      
        ADDI   -> List(Y,        N,        N,       N,    DC,            RS1,       IMM,       ITYPE,     AluOp.ADD),
        ANDI   -> List(Y,        N,        N,       N,    DC,            RS1,       IMM,       ITYPE,     AluOp.AND),
        ORI    -> List(Y,        N,        N,       N,    DC,            RS1,       IMM,       ITYPE,     AluOp.OR),
        XORI   -> List(Y,        N,        N,       N,    DC,            RS1,       IMM,       ITYPE,     AluOp.XOR),
        SLTIU  -> List(Y,        N,        N,       N,    DC,            RS1,       IMM,       ITYPE,     AluOp.SLTU),
        SLTI   -> List(Y,        N,        N,       N,    DC,            RS1,       IMM,       ITYPE,     AluOp.SLT),
        SRAI   -> List(Y,        N,        N,       N,    DC,            RS1,       IMM,       ITYPE,     AluOp.SRA),
        SRLI   -> List(Y,        N,        N,       N,    DC,            RS1,       IMM,       ITYPE,     AluOp.SRL),
        SLLI   -> List(Y,        N,        N,       N,    DC,            RS1,       IMM,       ITYPE,     AluOp.SLL),
        LUI    -> List(Y,        N,        N,       N,    DC,            DC,        IMM,       UTYPE,     AluOp.COPY_B),
        AUIPC  -> List(Y,        N,        N,       N,    DC,            PC,        IMM,       UTYPE,     AluOp.ADD),
            
        JAL    -> List(Y,        N,        N,       Y,    DC,            PC,        N4,        DC,        AluOp.ADD),
        JALR   -> List(Y,        N,        N,       Y,    DC,            PC,        N4,        DC,        AluOp.ADD),
            
        BLT    -> List(N,        N,        Y,       N,    branchType.lt, DC,        DC,        DC,        DC),   
        BLTU   -> List(N,        N,        Y,       N,    branchType.ltu,DC,        DC,        DC,        DC),   
        BEQ    -> List(N,        N,        Y,       N,    branchType.eq, DC,        DC,        DC,        DC),   
        BGE    -> List(N,        N,        Y,       N,    branchType.ge, DC,        DC,        DC,        DC),   
        BGEU   -> List(N,        N,        Y,       N,    branchType.geu,DC,        DC,        DC,        DC),   
        BNE    -> List(N,        N,        Y,       N,    branchType.ne, DC,        DC,        DC,        DC),   
        
    )


    val invaild = List(N, N, N, N, DC, DC, DC, DC, AluOp.INVAILD)

    val decodedControlSignals = ListLookup(
        io.ins.asUInt(),
        invaild,
        opcodeMap
    )
    
    when(decodedControlSignals(8)===AluOp.INVAILD){
        printf(s"INVAILD INSTRUCTION\n")
        printf("%b\n",io.ins.asUInt())
        assert(false.B)
    } 
    

    io.ctrl_signal.regWrite   := decodedControlSignals(0)
    io.ctrl_signal.memOp      := decodedControlSignals(1)
    io.ctrl_signal.branch     := decodedControlSignals(2)
    io.ctrl_signal.jump       := decodedControlSignals(3)
    
    io.branch_type:= decodedControlSignals(4)
    io.op_1_type  := decodedControlSignals(5)
    io.op_2_type  := decodedControlSignals(6)
    io.imm_type   := decodedControlSignals(7)
    io.alu_op     := decodedControlSignals(8)
}
