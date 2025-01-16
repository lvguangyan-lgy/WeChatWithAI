package com.rgzn.ttd.service;
import com.rgzn.ttd.dto.ColumnMeteDto;
import com.rgzn.ttd.model.ColumnMete;
import com.rgzn.ttd.core.Service;

import java.util.List;


/**
 * Created by lgy on 2024/11/04.
 */
public interface ColumnMeteService extends Service<ColumnMete> {

    List<ColumnMeteDto> findByTableId(String tableId);

}
