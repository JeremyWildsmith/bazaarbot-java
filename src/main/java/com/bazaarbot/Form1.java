//
// Translated by CS2J (http://www.cs2j.com): 2019-08-12 9:59:31 PM
//

package com.bazaarbot;
/*
public class Form1 extends Form
{

    private Economy economy;
    private Market market;
    //	    private MarketDisplay display;
    //	    private TextField txt_benchmark;
    public Form1() throws Exception {
        initializeComponent();
    }

    private void button1_Click(Object sender, EventArgs e) throws Exception {
        economy = new DoranAndParberryEconomy();
        market = economy.getMarket("default");
        dataGridView1.DataSource = market._agents;
    }

    //dataGridView2.DataSource = market._book.dbook;
    private void run(int rounds) throws Exception {
        market.simulate(rounds);
        MarketReport res = market.get_marketReport(rounds);
        dataGridView1.Refresh();
        //dataGridView2.DataSource = res.arrStrListInventory;
        textBox1.Clear();
        textBox1.Text = res.strListGood.replace("\n", "  ") + System.getProperty("line.separator");
        textBox1.Text += res.strListGoodPrices.replace("\n", "  ") + System.getProperty("line.separator");
        textBox1.Text += res.strListGoodTrades.replace("\n", "  ") + System.getProperty("line.separator");
        textBox1.Text += res.strListGoodBids.replace("\n", "  ") + System.getProperty("line.separator");
        textBox1.Text += res.strListGoodAsks.replace("\n", "  ") + System.getProperty("line.separator");
    }

    //textBox1.Lines = res.arrStrListInventory.ToArray<string>();
    //dataGridView1.DataSource = market._agents;
    private void button2_Click(Object sender, EventArgs e) throws Exception {
        run(1);
    }

    private void button3_Click(Object sender, EventArgs e) throws Exception {
        run(20);
    }

    private System.ComponentModel.IContainer components = null;


    protected void dispose(boolean disposing) throws Exception {
        if (disposing && (components != null))
        {
            components.Dispose();
        }
         
        super.Dispose(disposing);
    }


    private void initializeComponent() throws Exception {
        this.button1 = new System.Windows.Forms.Button();
        this.button2 = new System.Windows.Forms.Button();
        this.dataGridView1 = new System.Windows.Forms.DataGridView();
        this.dataGridView2 = new System.Windows.Forms.DataGridView();
        this.textBox1 = new System.Windows.Forms.TextBox();
        this.button3 = new System.Windows.Forms.Button();
        this.tbLog = new System.Windows.Forms.TextBox();
        ((System.ComponentModel.ISupportInitialize)(this.dataGridView1)).BeginInit();
        ((System.ComponentModel.ISupportInitialize)(this.dataGridView2)).BeginInit();
        this.SuspendLayout();
        //
        // button1
        //
        this.button1.Location = new System.Drawing.Point(12, 23);
        this.button1.Name = "button1";
        this.button1.Size = new System.Drawing.Size(75, 23);
        this.button1.TabIndex = 0;
        this.button1.Text = "Init";
        this.button1.UseVisualStyleBackColor = true;
        this.button1.Click += new System.EventHandler(this.button1_Click);
        //
        // button2
        //
        this.button2.Location = new System.Drawing.Point(12, 63);
        this.button2.Name = "button2";
        this.button2.Size = new System.Drawing.Size(42, 23);
        this.button2.TabIndex = 1;
        this.button2.Text = "Step";
        this.button2.UseVisualStyleBackColor = true;
        this.button2.Click += new System.EventHandler(this.button2_Click);
        //
        // dataGridView1
        //
        this.dataGridView1.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
        this.dataGridView1.Location = new System.Drawing.Point(0, 123);
        this.dataGridView1.Name = "dataGridView1";
        this.dataGridView1.Size = new System.Drawing.Size(604, 487);
        this.dataGridView1.TabIndex = 3;
        //
        // dataGridView2
        //
        this.dataGridView2.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
        this.dataGridView2.Location = new System.Drawing.Point(325, 123);
        this.dataGridView2.Name = "dataGridView2";
        this.dataGridView2.Size = new System.Drawing.Size(328, 487);
        this.dataGridView2.TabIndex = 4;
        //
        // textBox1
        //
        this.textBox1.Location = new System.Drawing.Point(130, 23);
        this.textBox1.Multiline = true;
        this.textBox1.Name = "textBox1";
        this.textBox1.Size = new System.Drawing.Size(512, 94);
        this.textBox1.TabIndex = 5;
        //
        // button3
        //
        this.button3.Location = new System.Drawing.Point(60, 63);
        this.button3.Name = "button3";
        this.button3.Size = new System.Drawing.Size(64, 23);
        this.button3.TabIndex = 6;
        this.button3.Text = "Step 20";
        this.button3.UseVisualStyleBackColor = true;
        this.button3.Click += new System.EventHandler(this.button3_Click);
        //
        // tbLog
        //
        this.tbLog.Location = new System.Drawing.Point(0, 616);
        this.tbLog.Multiline = true;
        this.tbLog.Name = "tbLog";
        this.tbLog.Size = new System.Drawing.Size(653, 80);
        this.tbLog.TabIndex = 7;
        //
        // Form1
        //
        this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
        this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
        this.ClientSize = new System.Drawing.Size(687, 697);
        this.Controls.Add(this.tbLog);
        this.Controls.Add(this.button3);
        this.Controls.Add(this.textBox1);
        this.Controls.Add(this.dataGridView1);
        this.Controls.Add(this.button2);
        this.Controls.Add(this.button1);
        this.Controls.Add(this.dataGridView2);
        this.Name = "Form1";
        this.Text = "Form1";
        ((System.ComponentModel.ISupportInitialize)(this.dataGridView1)).EndInit();
        ((System.ComponentModel.ISupportInitialize)(this.dataGridView2)).EndInit();
        this.ResumeLayout(false);
        this.PerformLayout();
    }

    private System.Windows.Forms.Button button1 = new System.Windows.Forms.Button();
    private System.Windows.Forms.Button button2 = new System.Windows.Forms.Button();
    private System.Windows.Forms.DataGridView dataGridView1 = new System.Windows.Forms.DataGridView();
    private System.Windows.Forms.DataGridView dataGridView2 = new System.Windows.Forms.DataGridView();
    private System.Windows.Forms.TextBox textBox1 = new System.Windows.Forms.TextBox();
    private System.Windows.Forms.Button button3 = new System.Windows.Forms.Button();
    private System.Windows.Forms.TextBox tbLog = new System.Windows.Forms.TextBox();

}


*/