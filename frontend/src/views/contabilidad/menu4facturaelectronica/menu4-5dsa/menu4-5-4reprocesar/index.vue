<template>
  <el-container>
    <el-header>
      <h3>REPROCESAR DOCUMENTO SOPORTE</h3>
    </el-header>
    <el-main>
      <el-row>
        <el-col :span="24">
          <el-tabs type="border-card">
            <el-tab-pane label="Fecha">
              <el-row :gutter="4">
                <el-col :span="24">
                  <h4>Rango de Fechas</h4>
                </el-col>
              </el-row>
              <el-row :gutter="4">
                <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                  <el-date-picker v-model="startDate" type="date" placeholder="Fecha de Inicio"></el-date-picker>
                </el-col>
                <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                  <el-date-picker v-model="endDate" type="date" placeholder="Fecha de Fin"></el-date-picker>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <el-button type="primary" icon="el-icon-search" @click="busquedaFecha()">Buscar</el-button>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <el-button
                    v-if="hayDatos"
                    type="warning"
                    icon="el-icon-refresh-right"
                    @click="confirmarProcesar()"
                  >Procesar</el-button>
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane label="Número">
              <el-row :gutter="4">
                <el-col :span="24">
                  <h4>Rango de Números</h4>
                </el-col>
              </el-row>
              <el-row :gutter="4">
                <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                  <el-input v-model="startNumber" type="number" placeholder="Número de Inicio"></el-input>
                </el-col>
                <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                  <el-input v-model="endNumber" type="number" placeholder="Número de Fin"></el-input>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <el-button type="primary" icon="el-icon-search" @click="busquedaNumero()">Buscar</el-button>
                </el-col>
                <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
                  <el-button
                    v-if="hayDatos"
                    type="warning"
                    icon="el-icon-refresh-right"
                    @click="confirmarProcesar()"
                  >Procesar</el-button>
                </el-col>
              </el-row>
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-table
            :data="tableData.slice(offset, pageSize + offset)"
            style="width: 100%"
            :row-class-name="tableRowClassName"
          >
            <el-table-column prop="fact_numero" label="Número" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.FACT_NUMERO }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="fact_fecha" label="Fecha" width="100">
              <template slot-scope="scope">
                <span>{{ scope.row.FACT_FECHA | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="fact_descripcion" label="Descripción" width="300">
              <template slot-scope="scope">
                <span>{{ scope.row.FACT_DESCRIPCION | fm_truncate(40) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="id_comprobante" label="Comprobante" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.ID_COMPROBANTE}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="id_persona" label="Persona" width="400">
              <template slot-scope="scope">
                <span :title="scope.row.NOMBRE + ' ' + scope.row.PRIMER_APELLIDO">{{ scope.row.NOMBRE + ' ' + scope.row.PRIMER_APELLIDO | fm_truncate(40) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="response" label="Estado">
              <template slot-scope="scope">
                <span>{{ scope.row.response }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-switch v-model="hiddenOnSinglePage"></el-switch>
          <el-pagination
            :hide-on-single-page="hiddenOnSinglePage"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page.sync="currentPage"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="totalItems"
          ></el-pagination>
        </el-col>
      </el-row>
    </el-main>
    <el-dialog :visible.sync="showConfirmarDlg" title="Confirmar Reproceso de Facturas">
      <el-row :guter="4">
        <el-col :span="24">
          <span>Seguro de Enviar las Facturas el Proveedor Tecnológico?</span>
        </el-col>
      </el-row>
      <el-row :gutter="4" type="flex" justify="end">
        <el-col :span="3">
          <el-button type="primary" @click="procesar()">Si</el-button>
        </el-col>
        <el-col :span="3">
          <el-button type="danger" @click="showConfirmarDlg=!showConfirmarDlg">No</el-button>
        </el-col>
      </el-row>
    </el-dialog>
  </el-container>
</template>
<script>
import {
  obtenerFacturaPorRangoFecha,
  obtenerFacturaPorRangoNumero
} from "@/api2/factura";
import { enviarFactura } from "@/api/http";

export default {
  data() {
    return {
      startDate: null,
      endDate: null,
      startNumber: null,
      endNumber: null,
      tableData: [],
      loader: null,
      hayDatos: false,
      showConfirmarDlg: false,
      hiddenOnSinglePage: true,
      pageSize: 10,
      currentPage: 1,
      totalItems: null,
      offset: 0
    };
  },
  methods: {
    tableRowClassName({ row }) {
      if (row.response === "Error") {
        return "warning-row";
      } else if (row.response === "Procesado") {
        return "success-row";
      }
      return "";
    },
    handleSizeChange(val) {
      console.log(`${val} items per page`);
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.offset = (this.pageSize * (this.currentPage - 1))
      console.log(`offset: ${this.offset}`);
    },
    busquedaFecha() {
      this.mostrarLoader();
      this.startDate.setHours(0);
      this.startDate.setMinutes(0);
      this.startDate.setSeconds(0);
      this.endDate.setHours(23);
      this.endDate.setMinutes(59);
      this.endDate.setSeconds(59);
      obtenerFacturaPorRangoFecha(
        this.startDate.getTime(),
        this.endDate.getTime()
      )
        .then(response => {
          this.tableData = response.data.map(v => {
            this.$set(v, "response", "Para Enviar"); // https://vuejs.org/v2/guide/reactivity.html
            return v;
          });
          if (this.tableData.length > 0) {
            this.hayDatos = true;
            this.totalItems = this.tableData.length;
          } else {
            this.hayDatos = false;
            this.totalItems = 0;
          }
          this.ocultarLoader();
        })
        .catch(error => {
          this.ocultarLoader();
          this.$alert(
            "Facturas no encontradas en el rango: " + error,
            "Buscando Facturas"
          );
        });
    },
    busquedaNumero() {
      this.mostrarLoader();
      obtenerFacturaPorRangoNumero(this.startNumber, this.endNumber)
        .then(response => {
          this.tableData = response.data.map(v => {
            this.$set(v, "response", "Para Enviar"); // https://vuejs.org/v2/guide/reactivity.html
            return v;
          });
          if (this.tableData.length > 0) {
            this.hayDatos = true;
            this.totalItems = this.tableData.length;
          } else {
            this.hayDatos = false;
            this.totalItems = 0;
          }
          this.ocultarLoader();
        })
        .catch(error => {
          this.ocultarLoader();
          this.$alert(
            "Facturas no encontradas en el rango: " + error,
            "Buscando Facturas"
          );
        });
    },
    confirmarProcesar() {
      this.showConfirmarDlg = true;
    },
    procesar() {
      this.showConfirmarDlg = false;
      this.tableData.forEach(f => {
        f.response = "Enviando...";
        enviarFactura(f.FACT_NUMERO)
          .then(response => {
            const SetDocumentResult = response.data.SetDocumentResult;
            if (SetDocumentResult.DetalleRespuesta === "Ok") {
              f.response = "Procesado";
            } else {
              f.response = "Error Respuesta";
            }
          })
          .catch(() => {
            f.response = "Error Conexión";
          });
      });
    },
    mostrarLoader() {
      this.loader = this.$loading({
        lock: true,
        text: "Cargando",
        background: "rgba(0, 0, 0, 0.7)"
      });
    },
    ocultarLoader() {
      if (this.loader) {
        this.loader.close();
      }
    }
  }
};
</script>
<style lang="scss" scoped>
.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}
</style>
