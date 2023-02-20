<template>
  <el-container>
    <el-main>
      <el-container>
        <el-header class="gestion_header">
          <h1>{{ $t("notacredito.title") }}</h1>
        </el-header>
        <el-main>
          <query-builder
            :labels="qlabels"
            :rules="qrules"
            :styled="qstyled"
            :maxDepth="3"
            v-model="qbquery"
          ></query-builder>
          <el-button
            type="warning"
            icon="el-icon-search"
            circle
            @click="actualizar"
            title="Actualizar Aplicando el Filtro"
          ></el-button>
        </el-main>
      </el-container>
      <el-container>
        <el-row>
          <el-col>
            <el-table
              v-loading="loading"
              :data="tableData"
              highlight-current-row
              style="width: 100%; font-size: 12px;"
              max-height="350"
              @sort-change="handleSort"
              @filter-change="handleFilter"
            >
              <el-table-column type="selection" width="35" />
              <el-table-column type="expand" width="35">
                <template slot-scope="scope">
                  <el-table
                    :data="scope.row.items"
                    :summary-method="getSummaries"
                    show-summary
                  >
                    <el-table-column label="Detalle" width="350">
                      <template slot-scope="item">
                        <span>{{ item.row.fanoit_detalle }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      label="Vr. Unitario"
                      align="right"
                      width="120"
                    >
                      <template slot-scope="item">
                        <span>{{
                          item.row.fanoit_valorunitario | currency_2("$")
                        }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Qty" width="80">
                      <template slot-scope="item">
                        <span>{{ item.row.fanoit_cantidad }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Tasa Iva" width="80">
                      <template slot-scope="item">
                        <span>{{ item.row.fanoit_tasaiva }}%</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="Vr. Iva" align="right" width="120">
                      <template slot-scope="item">
                        <span>{{
                          item.row.fanoit_valoriva | currency_2("$")
                        }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      prop="fait_total"
                      label="Vr. Total"
                      align="right"
                      width="120"
                    >
                      <template slot-scope="item">
                        <span>{{ item.row.fanoit_total | currency_2("$") }}</span>
                      </template>
                    </el-table-column>
                  </el-table>
                </template>
              </el-table-column>
              <el-table-column label="Número">
                <template slot-scope="scope">
                  <span>{{ scope.row.fact_nota_numero }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Fecha" width="150">
                <template slot-scope="scope">
                  <span>{{ scope.row.fact_nota_fecha | moment("YYYY-MM-DD") }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Descripción" width="350">
                <template slot-scope="scope">
                  <span>{{ scope.row.fact_nota_descripcion }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Factura" width="100">
                <template slot-scope="scope">
                  <span>{{ scope.row.fact_numero }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Tp. Id.">
                <template slot-scope="scope">
                  <span>{{ scope.row.id_identificacion }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Identificación" width="110">
                <template slot-scope="scope">
                  <span>{{ scope.row.id_persona }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Primer Apellido" width="110">
                <template slot-scope="scope">
                  <span>{{ scope.row.primer_apellido }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Segundo Apellido" width="125">
                <template slot-scope="scope">
                  <span>{{ scope.row.segundo_apellido }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Nombre" width="200">
                <template slot-scope="scope">
                  <span>{{ scope.row.nombre }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Total" width="120" align="right">
                <template slot-scope="scope">
                  <span>{{ scope.row.fact_nota_total | currency_2("$") }}</span>
                </template>
              </el-table-column>
            <el-table-column
              fixed="right"
              label=""
              width="120">
              <template slot-scope="scope">
                <el-dropdown size="mini" @command="handleCommand">
                  <el-button size="mini" type="primary">
                    Acciones<i class="el-icon-arrow-down el-icon--right"></i>
                  </el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item icon="el-icon-view" :command="{ 'method': 1, 'param': { 'fact_nota_numero': scope.row.fact_nota_numero, 'fact_nota_fecha': scope.row.fact_fecha } }">Ver Pdf</el-dropdown-item>
                    <el-dropdown-item icon="el-icon-upload" :command="{ 'method': 2, 'param': scope.row.fact_nota_numero }">Reenviar</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </el-table-column>
            </el-table>
            <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :page-size="page_size"
              layout="sizes, prev, pager, next, total"
              :total="total"
            >
            </el-pagination>
          </el-col>
        </el-row>
      </el-container>
      <el-row>
        <el-col :span="24">
          <el-button type="primary" @click="drawer = true">Nueva Nota Crédito</el-button>
        </el-col>
      </el-row>
    </el-main>
    <el-drawer
      :visible.sync="drawer"
      :with-header="false"
      :show-close="true"
      direction="rtl"
      size="80%"
      :destroy-on-close="true">
      <nota-factura :tipo="'C'" />
    </el-drawer>
  </el-container>
</template>
<script>
import VueQueryBuilder from "vue-query-builder"
import NotaFactura from '@/components/NotaFactura'
import { mapGetters } from "vuex"

import { currency } from "@/utils/math"
import { getNotasCredito, enviarNotaCredito, getFacturaProveedor } from "@/api/factura"

export default {
  components: {
    "query-builder": VueQueryBuilder,
    "nota-factura": NotaFactura
  },
  computed: {
    ...mapGetters(["empresa", "usuario"])
  },
  data() {
    return {
      loading: false,
      tableData: [],
      drawer: false,
      qbquery: {},
      qrules: [
        {
          type: "custom",
          id: "fact_numero",
          label: this.$i18n.t("factura.numero"),
          operators: ["=", "<>", "<", "<=", ">", ">="]
        },
        {
          type: "text",
          inputType: "date",
          id: "fact_fecha",
          default: new Date(),
          label: this.$i18n.t("factura.fecha"),
          operators: ["contiene a"]
        }
      ],
      qlabels: {
        matchType: this.$i18n.t("qb.matchType"),
        matchTypes: [
          {
            id: "all",
            label: this.$i18n.t("qb.matchTypeAll")
          },
          {
            id: "any",
            label: this.$i18n.t("qb.matchTypeAny")
          }
        ],
        addRule: this.$i18n.t("qb.addRule"),
        removeRule: this.$i18n.t("qb.removeRule"),
        addGroup: this.$i18n.t("qb.addGroup"),
        removeGroup: this.$i18n.t("qb.removeGroup"),
        textInputPlaceholder: this.$i18n.t("qb.textInputPlaceholder")
      },
      qstyled: true,
      page_size: 30,
      current_page: 1,
      total: 0,
      order: "",
      filter: "",
      factura_pdf: null,
      fact_cufe: null,
      facturaVisible: null
    };
  },
  beforeMount() {
    this.getData();
  },
  methods: {
    handleFilter(filters) {
      this.filter = JSON.stringify(filters);
      this.getData();
    },
    handleCurrentChange(val) {
      this.current_page = val;
      this.getData();
    },
    handleSizeChange(val) {
      this.page_size = val;
      this.getData();
    },
    handleSort({ column, prop, order }) {
      console.log("column:" + JSON.stringify(column));
      console.log("prop:" + prop);
      console.log("order:" + order);
      if (prop !== null) {
        if (order === "ascending") {
          this.order = prop + " asc";
        } else {
          this.order = prop + " desc";
        }
        this.filter = this.qbquery;
        console.log("filter:" + JSON.stringify(this.filter));
        this.getData();
      }
    },
    getSummaries(param) {
      const { columns, data } = param;
      const sums = [];
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = "Total";
          return;
        }
        const values = data.map(item => Number(item[column.property]));
        if (!values.every(value => isNaN(value))) {
          sums[index] =
            "$" +
            currency(
              values.reduce((prev, curr) => {
                const value = Number(curr);
                if (!isNaN(value)) {
                  return prev + curr;
                } else {
                  return prev;
                }
              }, 0),
              0
            );
        } else {
          sums[index] = "-";
        }
      });

      return sums;
    },
    actualizar() {
      this.filter = this.qbquery;
      this.getData();
    },
    getData() {
      getNotasCredito(
        this.page_size,
        this.current_page,
        this.filter,
        this.order
      ).then(response => {
        this.total = response.data.total;
        this.tableData = response.data.data;
      });
    },
    reEnviar(fact_nota_numero) {
      this.$confirm('Seguro de Reenviar la Nota Crédito ' + fact_nota_numero + '?', 'Confirmación', {
          confirmButtonText: 'Si',
          cancelButtonText: 'No',
          type: 'warning'
        }).then(() => {
          enviarNotaCredito(fact_nota_numero).then(response => {
            if (response.status === 200) {
              // se debe verificar la aceptacion
              this.$notify.info({
                title: 'Información',
                message: 'Nota Crédito Reenviada'
              })
            }
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: 'Reenvio cancelado'
          });          
        });
    },
    handleCommand(command) {
      switch (command.method) {
        case 1: this.verPdf(command.param)
           break
        case 2: this.reEnviar(command.param)
           break
      }
    },
    verPdf (param) {
      console.log("param:" + JSON.stringify(param))
      this.$notify.info({
        title: 'Información',
        message: 'Estamos contactando con el proveedor tecnológico'
      })
      const fact_nota_numero = param.fact_nota_numero
      const fact_nota_fecha = param.fact_nota_fecha
      const periodo = this.$moment(fact_nota_fecha).format('YYYYMM')
      getFacturaProveedor(fact_nota_numero, periodo).then(response => {
        this.fact_cufe = response.data.GetTransaccionbyIdentificacionResult.CodigoTransaccion
        if (this.fact_cufe) {
          this.factura_pdf = 'data:application/pdf;base64,' + response.data.GetTransaccionbyIdentificacionResult.PDFBase64
          this.facturaVisible = true
        } else {
          this.$notify.error({
            title: 'Error',
            message: 'Nota Crédito NO Está en el Proveedor Tecnológico'
          })
        }
      })
    }    
  }
};
</script>
