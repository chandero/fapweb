<template>
  <el-container>
    <el-header>
      CONSULTA DE CAUSACION DE COLOCACION
    </el-header>
    <el-main>
      <el-form label-position="left">
        <el-form-item label="Colocación">
          <el-input v-model="id_colocacion" />
        </el-form-item>
        <el-form-item>
          <el-button @click="consultar">Consultar</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="tableData" :expand-change="extracto" stripe style="width: 100%" max-height="400" >
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-table :data="props.row.extracto" stripe>
              <el-table-column type="expand">
                <template slot-scope="props">
                  <el-table :data="props.row.detallado" stripe max-height="300" >
                    <el-table-column label="Código" width="200" >
                      <template slot-scope="scope">
                        <span style="margin-left: 10px">{{ scope.row.codigo }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Nombre" width="350" style="font-size: 12px;">
                      <template slot-scope="scope">
                        <span style="margin-left: 10px">{{ scope.row.nombre }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Fecha Inicial" width="100">
                      <template slot-scope="scope">
                        <span style="margin-left: 10px">{{ scope.row.fecha_inicial | moment("YYYY-MM-DD") }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Fecha Final" width="100">
                      <template slot-scope="scope">
                        <span style="margin-left: 10px">{{ scope.row.fecha_final | moment("YYYY-MM-DD") }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Días" width="60">
                      <template slot-scope="scope">
                        <span style="margin-left: 10px">{{ scope.row.dias }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Tasa" width="60" align="right">
                      <template slot-scope="scope">
                        <span style="margin-left: 10px">{{ scope.row.tasa | currency }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Débito" width="100" align="right">
                      <template slot-scope="scope">
                        <span style="margin-left: 10px">{{ scope.row.debito | currency }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Crédito" width="100" align="right">
                      <template slot-scope="scope">
                        <span style="margin-left: 10px">{{ scope.row.credito | currency }}</span>
                      </template>
                    </el-table-column>
                  </el-table>
                </template>
              </el-table-column>
              <el-table-column label="Fecha Abono">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.fecha | moment("YYYY-MM-DD") }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Comprobante">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.cbte }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Abono Capital">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.capital | currency }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Int. Anticipado">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.anticipado | currency }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Int. Causado">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.cxc | currency }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Int. Servicios">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.servicio | currency }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Fecha Interés">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.fecha | moment("YYYY-MM-DD") }}</span>
                </template>
              </el-table-column>
            </el-table>
          </template>
        </el-table-column>
        <el-table-column label="Fecha" prop="fecha" >
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.fecha | moment("YYYY-MM-DD") }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Anticipado" prop="anticipado">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.anticipado | currency }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Causado" prop="causado" >
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.causado | currency }}</span>
          </template>
        </el-table-column>
        <el-table-column label="No Causado" prop="contingencia">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.contingencia | currency }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Días" prop="dias">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.dias }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Tasa" prop="tasa">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.tasa | currency }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Fecha Interés" prop="fecha_interes">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.fecha_interes | moment("YYYY-MM-DD") }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>
<script>
import { consultarCausacion, extractoCausacion } from '@/api/informe'

export default {
  data() {
    return {
      id_colocacion: null,
      tableData: [],
      loading: false
    }
  },
  methods: {
    consultar() {
      this.loading = true
      consultarCausacion(this.id_colocacion).then(response => {
        this.loading = false
        this.tableData = response.data
      })
    },
    extracto(row, data) {
      console.log('row: ' + row)
      console.log('data: ' + data)
      extractoCausacion()
    }
  }
}
</script>
<style lang="scss" scoped>
.el-table {
  font-size: 12px;
  padding-right: 0px;
  padding-left: 0px;
}
.el-table__expanded-cell {
    padding: 0px 20px;
}
</style>

