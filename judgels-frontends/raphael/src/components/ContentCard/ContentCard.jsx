import { Card } from '@blueprintjs/core';
import * as React from 'react';

import './ContentCard.css';

export function ContentCard({ className, elevation, children }) {
  return (
    <div className={className}>
      <Card className="content-card" elevation={elevation}>
        {children}
      </Card>
    </div>
  );
}
