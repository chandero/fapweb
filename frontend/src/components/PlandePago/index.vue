<template>
  <el-container>
    <el-main>
      <el-row>
        <el-col :span="24">
          <el-table :data="datos" :show-summary="false" :summary-method="totales" :row-key="row => row.CUOTA_NUMERO" sum-text="TOT" stripe style="width: 100%; font-size: 12px;" max-height="450">
            <el-table-column label="Cuota" prop="cuota_numero" align="right" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.CUOTA_NUMERO }}</span>
              </template>
            </el-table-column>            
            <el-table-column sortable label="Fecha" prop="fecha_a_pagar" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.FECHA_A_PAGAR | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Capital" prop="capital_a_pagar" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.CAPITAL_A_PAGAR | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Interés" prop="interes_a_pagar" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.INTERES_A_PAGAR | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Pagada" prop="pagada" align="right" width="150">
              <template slot-scope="scope">
                <el-checkbox :value="!!scope.row.PAGADA"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column label="F.Pagada" prop="fecha_pagada" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.FECHA_PAGADA | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-button type="primary" @click="Cerrar()">Cerrar</el-button>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
export default {
  name: 'PlanPagoColocacionComponent',
  props: {
    colocacion: {
      type: String,
      required: true
    },
    datos: {
      type: Array,
      required: true
    }
  },
  methods: {
    Cerrar() {
      this.$emit('cerrarPlanEvent')
    },
    totales(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 1) {
          sums[index] = 'Totales'
          return
        } else if (index !== 4) {
          return
        }
        const values = data.map(item => Number(item[column.property]))
        if (!values.every(value => isNaN(value))) {
          const total = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
          sums[index] = '$ ' + total.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.')
        } else {
          sums[index] = ''
        }
      })
      return sums
    }
  },    
}
</script>