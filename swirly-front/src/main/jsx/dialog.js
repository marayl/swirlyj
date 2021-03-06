/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/

var MarketDialog = React.createClass({
    // Mutators.
    reset: function() {
        this.setState(this.getInitialState());
    },
    setMarket: function(market) {
        this.setState({
            mnem: market.mnem,
            display: market.display,
            contr: market.contr.mnem,
            settlDate: market.settlDate,
            expiryDate: market.expiryDate,
            state: market.state,
            edit: true
        });
    },
    // DOM Events.
    onChangeMnem: function(event) {
        this.setState({
            mnem: event.target.value
        });
    },
    onChangeDisplay: function(event) {
        this.setState({
            display: event.target.value
        });
    },
    onChangeContr: function(event) {
        this.setState({
            contr: event.target.value
        });
    },
    onChangeSettlDate: function(event) {
        this.setState({
            settlDate: event.target.value
        });
    },
    onChangeExpiryDate: function(event) {
        this.setState({
            expiryDate: event.target.value
        });
    },
    onChangeState: function(event) {
        this.setState({
            state: event.target.value
        });
    },
    onFocusDisplay: function(event) {
        if (!isSpecified(this.state.display)) {
            this.setState({
                display: this.state.mnem
            });
        }
    },
    onFocusExpiryDate: function(event) {
        if (!isSpecified(this.state.expiryDate)) {
            this.setState({
                expiryDate: this.state.settlDate
            });
        }
    },
    onClickSave: function(event) {
        event.preventDefault();
        var state = this.state;
        var mnem = state.mnem;
        var display = state.display;
        var contr = state.contr;
        var settlDate = state.settlDate;
        var expiryDate = state.expiryDate;
        var mstate = state.state;
        var edit = state.edit;
        if (edit) {
            this.props.module.onPutMarket(mnem, display, mstate);
        } else {
            this.props.module.onPostMarket(mnem, display, contr, settlDate, expiryDate, mstate);
        }
        this.reset();
    },
    // Lifecycle.
    getInitialState: function() {
        return {
            mnem: undefined,
            display: undefined,
            contr: undefined,
            settlDate: undefined,
            expiryDate: undefined,
            state: 0,
            edit: false
        };
    },
    componentDidMount: function() {
        var node = this.refs.contr.getDOMNode();
        $(node).typeahead({
            items: 4,
            source: function(query, process) {
                var contrKeys = Object.keys(this.props.contrMap);
                process(contrKeys);
            }.bind(this),
            updater: function(value) {
                this.setState({
                    contr: value
                });
                return value;
            }.bind(this)
        });
    },
    render: function() {
        var state = this.state;
        var mnem = state.mnem;
        var display = state.display;
        var contr = state.contr;
        var settlDate = state.settlDate;
        var expiryDate = state.expiryDate;
        var mstate = state.state;
        var edit = state.edit;
        var title = edit ? 'Edit Market' : 'New Market';
        return (
            <div id="marketDialog" className="modal fade" tabindex={-1}>
              <div className="modal-dialog">
                <div className="modal-content">
                  <div className="modal-header">
                    <button type="button" className="close" data-dismiss="modal"
                            onClick={this.reset}>
                      {times}
                    </button>
                    <h4 className="modal-title">{title}</h4>
                  </div>
                  <div className="modal-body">
                    <form role="form" className="form-horizontal">
                      <div className="form-group">
                        <label htmlFor="mnem" className="col-sm-2 control-label">Mnem</label>
                        <div className="col-sm-10">
                          <input id="mnem" type="text" className="form-control" value={mnem}
                                 onChange={this.onChangeMnem} readOnly={edit}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="display" className="col-sm-2 control-label">Display</label>
                        <div className="col-sm-10">
                          <input id="display" type="text" className="form-control" value={display}
                                 onChange={this.onChangeDisplay} onFocus={this.onFocusDisplay}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="contr" className="col-sm-2 control-label">Contr</label>
                        <div className="col-sm-10">
                          <input id="contr" ref="contr" type="text" className="form-control"
                                 value={contr} onChange={this.onChangeContr} readOnly={edit}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="settlDate" className="col-sm-2 control-label">
                               Settl</label>
                        <div className="col-sm-10">
                          <input id="settlDate" type="date" className="form-control"
                                 value={settlDate} onChange={this.onChangeSettlDate}
                                 readOnly={edit}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="expiryDate" className="col-sm-2 control-label">
                               Expiry</label>
                        <div className="col-sm-10">
                          <input id="expiryDate" type="date" className="form-control"
                                 value={expiryDate} onChange={this.onChangeExpiryDate}
                                 onFocus={this.onFocusExpiryDate} readOnly={edit}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="state" className="col-sm-2 control-label">State</label>
                        <div className="col-sm-10">
                          <input id="state" type="number" className="form-control" value={mstate}
                                 onChange={this.onChangeState}/>
                        </div>
                      </div>
                    </form>
                  </div>
                  <div className="modal-footer">
                    <div className="btn-group">
                      <button type="button" className="btn btn-default" data-dismiss="modal"
                              onClick={this.reset}>
                        Cancel
                      </button>
                      <button type="button" className="btn btn-primary" data-dismiss="modal"
                              onClick={this.onClickSave}>
                        Save
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
        );
    }
});

var TraderDialog = React.createClass({
    // Mutators.
    reset: function() {
        this.setState(this.getInitialState());
    },
    setTrader: function(trader) {
        this.setState({
            mnem: trader.mnem,
            display: trader.display,
            email: trader.email,
            edit: true
        });
    },
    // DOM Events.
    onChangeMnem: function(event) {
        this.setState({
            mnem: event.target.value
        });
    },
    onChangeDisplay: function(event) {
        this.setState({
            display: event.target.value
        });
    },
    onChangeEmail: function(event) {
        this.setState({
            email: event.target.value
        });
    },
    onFocusDisplay: function(event) {
        if (!isSpecified(this.state.display)) {
            this.setState({
                display: this.state.mnem
            });
        }
    },
    onClickSave: function(event) {
        event.preventDefault();
        var state = this.state;
        var mnem = state.mnem;
        var display = state.display;
        var email = state.email;
        var edit = state.edit;
        if (edit) {
            this.props.module.onPutTrader(mnem, display, email);
        } else {
            this.props.module.onPostTrader(mnem, display, email);
        }
        this.reset();
    },
    // Lifecycle.
    getInitialState: function() {
        return {
            mnem: undefined,
            display: undefined,
            email: undefined,
            edit: false
        };
    },
    render: function() {
        var state = this.state;
        var mnem = state.mnem;
        var display = state.display;
        var email = state.email;
        var edit = state.edit;
        var title = edit ? 'Edit Trader' : 'New Trader';
        return (
            <div id="traderDialog" className="modal fade" tabindex={-1}>
              <div className="modal-dialog">
                <div className="modal-content">
                  <div className="modal-header">
                    <button type="button" className="close" data-dismiss="modal"
                            onClick={this.reset}>
                      {times}
                    </button>
                    <h4 className="modal-title">{title}</h4>
                  </div>
                  <div className="modal-body">
                    <form role="form" className="form-horizontal">
                      <div className="form-group">
                        <label htmlFor="mnem" className="col-sm-2 control-label">Mnem</label>
                        <div className="col-sm-10">
                          <input id="mnem" type="text" className="form-control" value={mnem}
                                 onChange={this.onChangeMnem} readOnly={edit}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="display" className="col-sm-2 control-label">Display</label>
                        <div className="col-sm-10">
                          <input id="display" type="text" className="form-control" value={display}
                                 onChange={this.onChangeDisplay} onFocus={this.onFocusDisplay}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="email" className="col-sm-2 control-label">Email</label>
                        <div className="col-sm-10">
                          <input id="email" type="email" className="form-control" value={email}
                                 onChange={this.onChangeEmail} readOnly={edit}/>
                        </div>
                      </div>
                    </form>
                  </div>
                  <div className="modal-footer">
                    <div className="btn-group">
                      <button type="button" className="btn btn-default" data-dismiss="modal"
                              onClick={this.reset}>
                        Cancel
                      </button>
                      <button type="button" className="btn btn-primary" data-dismiss="modal"
                              onClick={this.onClickSave}>
                        Save
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
        );
    }
});

var TradeDialog = React.createClass({
    // Mutators.
    reset: function() {
        this.setState(this.getInitialState());
    },
    setTrader: function(trader) {
        this.setState({
            trader: trader.mnem
        });
    },
    // DOM Events.
    onChangeTrader: function(event) {
        this.setState({
            trader: event.target.value
        });
    },
    onChangeMarket: function(event) {
        this.setState({
            market: event.target.value
        });
    },
    onChangeRef: function(event) {
        this.setState({
            ref: event.target.value
        });
    },
    onChangeSide: function(event) {
        this.setState({
            side: event.target.value
        });
    },
    onChangeLots: function(event) {
        this.setState({
            lots: event.target.value
        });
    },
    onChangePrice: function(event) {
        this.setState({
            price: event.target.value
        });
    },
    onChangeRole: function(event) {
        var role = event.target.value;
        if (role === 'N/A') {
            role = undefined;
        }
        this.setState({
            role: role
        });
    },
    onChangeCpty: function(event) {
        this.setState({
            cpty: event.target.value
        });
    },
    onClickSave: function(event) {
        event.preventDefault();
        var state = this.state;
        var trader = state.trader;
        var market = state.market;
        var ref = state.ref;
        var side = state.side;
        var lots = state.lots;
        var price = state.price;
        var role = state.role;
        var cpty = state.cpty;
        this.props.module.onPostTrade(trader, market, ref, side, lots, price, role, cpty);
        this.reset();
    },
    // Lifecycle.
    getInitialState: function() {
        return {
            trader: undefined,
            market: undefined,
            ref: undefined,
            side: undefined,
            lots: undefined,
            price: undefined,
            role: undefined,
            cpty: undefined
        };
    },
    componentDidMount: function() {
        var node = this.refs.market.getDOMNode();
        $(node).typeahead({
            items: 4,
            source: function(query, process) {
                var marketKeys = Object.keys(this.props.marketMap);
                process(marketKeys);
            }.bind(this),
            updater: function(value) {
                this.setState({
                    market: value
                });
                return value;
            }.bind(this)
        });
    },
    render: function() {
        var state = this.state;
        var trader = state.trader;
        var market = state.market;
        var ref = state.ref;
        var side = state.side;
        var lots = state.lots;
        var price = state.price;
        var role = state.role;
        var cpty = state.cpty;

        var contr = this.props.marketMap[market];
        var minLots = 1;
        var priceInc = 0.01;
        if (contr !== undefined) {
            minLots = contr.minLots;
            priceInc = contr.priceInc;
        }

        if (role === undefined) {
            role = 'N/A';
        }
        return (
            <div id="tradeDialog" className="modal fade" tabindex={-1}>
              <div className="modal-dialog">
                <div className="modal-content">
                  <div className="modal-header">
                    <button type="button" className="close" data-dismiss="modal"
                            onClick={this.reset}>
                      {times}
                    </button>
                    <h4 className="modal-title">New Trade</h4>
                  </div>
                  <div className="modal-body">
                    <form role="form" className="form-horizontal">
                      <div className="form-group">
                        <label htmlFor="trader" className="col-sm-2 control-label">Trader</label>
                        <div className="col-sm-10">
                          <input id="trader" type="text" className="form-control"
                                 value={trader} onChange={this.onChangeTrader}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="market" className="col-sm-2 control-label">Market</label>
                        <div className="col-sm-10">
                          <input id="market" ref="market" type="text" className="form-control"
                                 value={market} onChange={this.onChangeMarket}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="ref" className="col-sm-2 control-label">Ref</label>
                        <div className="col-sm-10">
                          <input id="ref" type="text" className="form-control" value={ref}
                                 onChange={this.onChangeRef}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <div className="col-sm-offset-2 col-sm-10">
                          <label className="radio-inline">
                            <input name="side" type="radio" value="BUY"
                                   checked={side ==='BUY'} onChange={this.onChangeSide}/>Buy
                          </label>
                          <label className="radio-inline">
                            <input name="side" type="radio" value="SELL"
                                   checked={side ==='SELL'} onChange={this.onChangeSide}/>Sell
                          </label>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="lots" className="col-sm-2 control-label">Lots</label>
                        <div className="col-sm-10">
                          <input id="lots" type="number" className="form-control" value={lots}
                                 onChange={this.onChangeLots} min={minLots}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="price" className="col-sm-2 control-label">Price</label>
                        <div className="col-sm-10">
                          <input id="price" type="number" className="form-control" value={price}
                                 onChange={this.onChangePrice} step={priceInc}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="role" className="col-sm-2 control-label">Role</label>
                        <div className="col-sm-10">
                          <select id="role" className="form-control" value={role}
                                 onChange={this.onChangeRole}>
                            <option>N/A</option>
                            <option>MAKER</option>
                            <option>TAKER</option>
                          </select>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="cpty" className="col-sm-2 control-label">Cpty</label>
                        <div className="col-sm-10">
                          <input id="cpty" type="text" className="form-control" value={cpty}
                                 onChange={this.onChangeCpty}/>
                        </div>
                      </div>
                    </form>
                  </div>
                  <div className="modal-footer">
                    <div className="btn-group">
                      <button type="button" className="btn btn-default" data-dismiss="modal"
                              onClick={this.reset}>
                        Cancel
                      </button>
                      <button type="button" className="btn btn-primary" data-dismiss="modal"
                              onClick={this.onClickSave}>
                        Save
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
        );
    }
});

var TransferDialog = React.createClass({
    // Mutators.
    reset: function() {
        this.setState(this.getInitialState());
    },
    setTrader: function(trader) {
        this.setState({
            trader: trader.mnem
        });
    },
    // DOM Events.
    onChangeTrader: function(event) {
        this.setState({
            trader: event.target.value
        });
    },
    onChangeMarket: function(event) {
        this.setState({
            market: event.target.value
        });
    },
    onChangeRef: function(event) {
        this.setState({
            ref: event.target.value
        });
    },
    onChangeSide: function(event) {
        this.setState({
            side: event.target.value
        });
    },
    onChangeLots: function(event) {
        this.setState({
            lots: event.target.value
        });
    },
    onClickSave: function(event) {
        event.preventDefault();
        var state = this.state;
        var trader = state.trader;
        var market = state.market;
        var ref = state.ref;
        var side = state.side;
        var lots = state.lots;
        var price = 0;
        var role = undefined;
        var cpty = undefined;
        this.props.module.onPostTrade(trader, market, ref, side, lots, price, role, cpty);
        this.reset();
    },
    // Lifecycle.
    getInitialState: function() {
        return {
            trader: undefined,
            market: undefined,
            ref: undefined,
            side: undefined,
            lots: undefined
        };
    },
    componentDidMount: function() {
        var node = this.refs.market.getDOMNode();
        $(node).typeahead({
            items: 4,
            source: function(query, process) {
                var marketKeys = Object.keys(this.props.marketMap);
                process(marketKeys);
            }.bind(this),
            updater: function(value) {
                this.setState({
                    market: value
                });
                return value;
            }.bind(this)
        });
    },
    render: function() {
        var state = this.state;
        var trader = state.trader;
        var market = state.market;
        var ref = state.ref;
        var side = state.side;
        var lots = state.lots;

        var contr = this.props.marketMap[market];
        var minLots = 1;
        if (contr !== undefined) {
            minLots = contr.minLots;
        }

        return (
            <div id="transferDialog" className="modal fade" tabindex={-1}>
              <div className="modal-dialog">
                <div className="modal-content">
                  <div className="modal-header">
                    <button type="button" className="close" data-dismiss="modal"
                            onClick={this.reset}>
                      {times}
                    </button>
                    <h4 className="modal-title">New Transfer</h4>
                  </div>
                  <div className="modal-body">
                    <form role="form" className="form-horizontal">
                      <div className="form-group">
                        <label htmlFor="trader" className="col-sm-2 control-label">Trader</label>
                        <div className="col-sm-10">
                          <input id="trader" type="text" className="form-control"
                                 value={trader} onChange={this.onChangeTrader}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="market" className="col-sm-2 control-label">Market</label>
                        <div className="col-sm-10">
                          <input id="market" ref="market" type="text" className="form-control"
                                 value={market} onChange={this.onChangeMarket}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="ref" className="col-sm-2 control-label">Ref</label>
                        <div className="col-sm-10">
                          <input id="ref" type="text" className="form-control" value={ref}
                                 onChange={this.onChangeRef}/>
                        </div>
                      </div>
                      <div className="form-group">
                        <div className="col-sm-offset-2 col-sm-10">
                          <label className="radio-inline">
                            <input name="side" type="radio" value="BUY"
                                   checked={side ==='BUY'} onChange={this.onChangeSide}/>
                                   Deposit
                          </label>
                          <label className="radio-inline">
                            <input name="side" type="radio" value="SELL"
                                   checked={side ==='SELL'} onChange={this.onChangeSide}/>
                                   Withdraw
                          </label>
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="lots" className="col-sm-2 control-label">Lots</label>
                        <div className="col-sm-10">
                          <input id="lots" type="number" className="form-control" value={lots}
                                 onChange={this.onChangeLots} min={minLots}/>
                        </div>
                      </div>
                    </form>
                  </div>
                  <div className="modal-footer">
                    <div className="btn-group">
                      <button type="button" className="btn btn-default" data-dismiss="modal"
                              onClick={this.reset}>
                        Cancel
                      </button>
                      <button type="button" className="btn btn-primary" data-dismiss="modal"
                              onClick={this.onClickSave}>
                        Save
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
        );
    }
});
